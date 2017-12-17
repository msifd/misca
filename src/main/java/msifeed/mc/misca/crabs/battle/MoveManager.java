package msifeed.mc.misca.crabs.battle;

import msifeed.mc.misca.crabs.actions.Action;
import msifeed.mc.misca.crabs.rules.ActionResult;
import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Rules;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EntityDamageSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public enum MoveManager {
    INSTANCE;

    // uuid защищяющегося -> ход атаковавшего
    private HashMap<UUID, Move> pendingMoves = new HashMap<>();
    private ArrayList<Move> completeMoves = new ArrayList<>();

    public void selectAction(FighterContext actor, Action action) {
        // Выбирать пассивные действия можно только при защите
        if (!actor.canSelectAction() || actor.target == null && action.type == Action.Type.PASSIVE) return;
        actor.updateAction(action);
    }

    public void describeAction(FighterContext actor) {
        actor.described = true;

        // Действия не требующие атаки завершаются сразу после отписи
        if (actor.action.dealNoDamage()) {
            stopDealingDamage(actor);
        }
    }

    public void dealDamage(FighterContext actor, FighterContext target, EntityDamageSource damageSource, float amount) {
        // Первый удар
        if (actor.status == FighterContext.Status.ACT) {
            actor.target = target.uuid;
            actor.updateStatus(FighterContext.Status.DEAL_DAMAGE);
            target.target = actor.uuid;
        }

        // TODO ограничение на скорость ударов (при текущей отмене урона они не ограничиваются)
        actor.damageDealt += amount;
    }

    public void stopDealingDamage(FighterContext actor) {
        actor.updateStatus(FighterContext.Status.WAIT);

        Move move = pendingMoves.get(actor.uuid);

        if (move == null) { // Боец атаковал
            move = new Move();
            move.attacker = actor;

            final FighterContext target = BattleManager.INSTANCE.getContext(actor.target);
            if (target.status == FighterContext.Status.KO_ED) {
                target.action = Action.ACTION_NONE;
                move.defender = target;
                completeMoves.add(move);
            } else {
                pendingMoves.put(actor.target, move);
            }
        } else { // Боец отвечал
            // TODO мультитаргет для остановки побега?
            move.defender = actor;
            completeMoves.add(move);
            pendingMoves.remove(actor.uuid);
        }
    }

    public void finalizeMoves() {
        for (Move m : completeMoves) finalizeMove(m.attacker, m.defender);
        completeMoves.clear();
    }

    private void finalizeMove(FighterContext attacker, FighterContext defender) {
        // Игнорируем бездельников
        if (attacker.action.type == Action.Type.PASSIVE && defender.action.type == Action.Type.PASSIVE) {
            attacker.reset(true);
            defender.reset(true);
            return;
        }

        // TODO плавающее ограничение на получаемый урон чтобы стимулировать нокауты
        final boolean isFatality = defender.status == FighterContext.Status.KO_ED;

        final ActionResult attack = new ActionResult(attacker);
        final ActionResult defence = new ActionResult(defender);
        final ActionResult winner;
        final ActionResult looser;
        if (isFatality) {
            winner = attack;
            looser = defence;
        } else {
            winner = Rules.computeWinner(attack, defence);
            looser = (winner == attack ? defence : attack);
        }

        for (Effect eff : winner.action.target_effects) eff.apply(winner.ctx, looser.ctx);
        for (Effect eff : winner.action.self_effects) eff.apply(winner.ctx, winner.ctx);
        // TODO handle some tags

        final String resultMsg = isFatality
                ? MoveFormatter.formatFatalityResult(winner.ctx.entity, looser.ctx.entity)
                : MoveFormatter.formatActionResults(winner, looser);

        MiscaUtils.notifyAround(
                winner.ctx.entity, looser.ctx.entity,
                BattleDefines.NOTIFICATION_RADIUS,
                new ChatComponentText(resultMsg)
        );

        // Если нокаут появляется после применения эффектов
        final boolean justKnockedOut = !isFatality && defender.status == FighterContext.Status.KO_ED;
        if (justKnockedOut) {
            final String msg = MiscaUtils.l10n("misca.crabs.knocked_out", defender.entity.getCommandSenderName());
            MiscaUtils.notifyAround(
                    winner.ctx.entity, looser.ctx.entity,
                    BattleDefines.NOTIFICATION_RADIUS,
                    new ChatComponentText(msg));
        }

        attacker.reset(true);
        defender.reset(!justKnockedOut);
    }

    private static class Move {
        FighterContext attacker, defender;
    }
}
