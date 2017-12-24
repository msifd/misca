package msifeed.mc.misca.crabs.fight;

import msifeed.mc.misca.crabs.action.Action;
import msifeed.mc.misca.crabs.context.Context;
import msifeed.mc.misca.crabs.context.ContextManager;
import msifeed.mc.misca.crabs.rules.ActionResult;
import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Rules;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EntityDamageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public enum MoveManager {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger("Crabs.Move");

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
        // TODO выяснить что я имел в виду под плавающим ограничением
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

        if (winner.actionSuccessful) {
            Rules.applyEffects(winner.action.target_effects, Effect.Stage.RESULT, looser, winner);
            Rules.applyEffects(winner.action.self_effects, Effect.Stage.RESULT, winner, looser);
        }

        // TODO handle some tags

        if (winner.actionSuccessful) {
            // Выдаем урон обоим, мало ли какие эффекты...
            if (winner.damageToReceive > 0) receiveDamage(winner, looser);
            if (looser.damageToReceive > 0) receiveDamage(looser, winner);

            // Если нокаут появляется после применения эффектов
            final boolean justKnockedOut = !isFatality && looser.ctx.knockedOut;
            if (justKnockedOut) {
                final String msg = MiscaUtils.l10n("misca.crabs.knocked_out", defender.entity.getCommandSenderName());
                MiscaUtils.notifyAround(
                        winner.ctx.entity, looser.ctx.entity,
                        BattleDefines.NOTIFICATION_RADIUS,
                        new ChatComponentText(msg));
            }
        }

        final String resultMsg = isFatality && winner.actionSuccessful
                ? MoveFormatter.formatFatalityResult(winner.ctx.entity, looser.ctx.entity)
                : MoveFormatter.formatActionResults(winner, looser);

        MiscaUtils.notifyAround(
                winner.ctx.entity, looser.ctx.entity,
                BattleDefines.NOTIFICATION_RADIUS,
                new ChatComponentText(resultMsg)
        );

        attacker.reset();
        defender.reset();

        ContextManager.INSTANCE.syncContext(attacker);
        ContextManager.INSTANCE.syncContext(defender);
    }

    /**
     * Тут наносится урон `себе`. Он накапливается от эффектов ранее.
     */
    private static void receiveDamage(ActionResult self, ActionResult enemy) {
        final Context selfCtx = self.ctx;
        final EntityLivingBase selfEntity = selfCtx.entity;
        final EntityLivingBase enemyEntity = enemy.ctx.entity;

        final float currentHealth = selfEntity.getHealth();
        final boolean isFatal = currentHealth <= self.damageToReceive;
        final float damageToDeal = isFatal && !selfCtx.knockedOut ? currentHealth - 1 : self.damageToReceive;

        selfEntity.attackEntityFrom(new CrabsDamage(enemyEntity), damageToDeal);
        if (isFatal) selfCtx.knockedOut = true;

        logger.info("`{}` received {} damage from `{}`", selfEntity.getCommandSenderName(), damageToDeal, enemyEntity.getCommandSenderName());
    }

    private static class Move {
        Context attacker, defender;
    }
}
