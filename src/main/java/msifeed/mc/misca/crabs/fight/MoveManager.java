package msifeed.mc.misca.crabs.fight;

import msifeed.mc.misca.crabs.action.Action;
import msifeed.mc.misca.crabs.context.Context;
import msifeed.mc.misca.crabs.context.ContextManager;
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

    public void selectAction(Context actor, Action action, int mod) {
        // Выбирать пассивные действия можно только при защите
        if (!actor.canSelectAction() || actor.target == null && action.type == Action.Type.PASSIVE) return;
        actor.updateAction(action);
        actor.modifier = mod;

        ContextManager.INSTANCE.syncContext(actor);
    }

    public void describeAction(Context actor) {
        if (actor.action == null) return;

        actor.described = true;

        // Действия не требующие атаки завершаются сразу после отписи
        if (actor.action.dealNoDamage()) {
            stopDealingDamage(actor);
        }
    }

    public void dealDamage(Context actor, Context target, EntityDamageSource damageSource, float amount) {
        // Первый удар
        if (actor.status == Context.Status.ACTIVE) {
            actor.target = target.uuid;
            actor.updateStatus(Context.Status.DEAL_DAMAGE);
            target.target = actor.uuid;
        }

        // TODO ограничение на скорость ударов (при текущей отмене урона они не ограничиваются)
        actor.damageDealt += amount;

        ContextManager.INSTANCE.syncContext(actor);
    }

    /**
     * Вызывается когда период атаки заканчивается
     */
    public void stopDealingDamage(Context actor) {
        actor.updateStatus(Context.Status.WAIT);
        ContextManager.INSTANCE.syncContext(actor);

        Move move = pendingMoves.get(actor.uuid);

        if (move == null) { // Боец атаковал
            move = new Move();
            move.attacker = actor;

            final Context target = ContextManager.INSTANCE.getContext(actor.target);
            if (target.knockedOut) {
                // Добивать можно без подтверждения
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

    private void finalizeMove(Context attacker, Context defender) {
        // Игнорируем бездельников
        if (attacker.action.type == Action.Type.PASSIVE && defender.action.type == Action.Type.PASSIVE) {
            attacker.reset();
            defender.reset();
            return;
        }

        // TODO плавающее ограничение на получаемый урон чтобы стимулировать нокауты
        final boolean isFatality = defender.knockedOut;

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

        winner.action.target_effects.forEach(e -> e.apply(Effect.Stage.RESULT, winner, looser));
        winner.action.self_effects.forEach(e -> e.apply(Effect.Stage.RESULT, winner, looser));
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
        final boolean justKnockedOut = !isFatality && looser.ctx.knockedOut;
        if (justKnockedOut) {
            final String msg = MiscaUtils.l10n("misca.crabs.knocked_out", defender.entity.getCommandSenderName());
            MiscaUtils.notifyAround(
                    winner.ctx.entity, looser.ctx.entity,
                    BattleDefines.NOTIFICATION_RADIUS,
                    new ChatComponentText(msg));
        }

        attacker.reset();
        defender.reset();
        looser.ctx.knockedOut = justKnockedOut;

        ContextManager.INSTANCE.syncContext(attacker);
        ContextManager.INSTANCE.syncContext(defender);
    }

    private static class Move {
        Context attacker, defender;
    }
}
