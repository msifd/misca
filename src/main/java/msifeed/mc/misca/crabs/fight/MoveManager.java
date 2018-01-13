package msifeed.mc.misca.crabs.fight;

import msifeed.mc.misca.crabs.action.Action;
import msifeed.mc.misca.crabs.context.Context;
import msifeed.mc.misca.crabs.context.ContextManager;
import msifeed.mc.misca.crabs.rules.ActionResult;
import msifeed.mc.misca.crabs.rules.Buff;
import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.database.DBHandler;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
        // Сбрасывать можно всегда
        if (actor.action != null && actor.action.name.equals(action.name)) {
            actor.updateAction(null);
        } else {
            // Выбирать пассивные действия можно только при защите
            if (!actor.canSelectAction() || (actor.target == null && action.dealNoDamage())) return;
            actor.updateAction(action);
            actor.modifier = mod;
        }

        ContextManager.INSTANCE.syncContext(actor);
    }

    public void selectMod(Context actor, int mod) {
        // Изменять мод. можно также только при защите
        if (!actor.canSelectAction()) return;
        actor.modifier = mod;

        ContextManager.INSTANCE.syncContext(actor);
    }

    public void describeAction(Context actor) {
        if (!actor.canSelectAction() || actor.action == null) return;

        actor.described = true;

        // Действия не требующие атаки завершаются сразу после отписи
        if (actor.target != null && actor.action.dealNoDamage()) {
            stopDealingDamage(actor);
        }

        ContextManager.INSTANCE.syncContext(actor);
    }

    public void dealDamage(Context actor, Context target, float amount) {
        // Первый удар
        if (actor.status == Context.Status.ACTIVE) {
            actor.target = target.uuid;
            actor.updateStatus(Context.Status.DEAL_DAMAGE);
            target.target = actor.uuid;

            ContextManager.INSTANCE.syncContext(target);
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
                target.updateAction(Action.ACTION_NONE);
                target.described = true;
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
        completeMoves.removeIf(m -> finalizeMove(m.attacker, m.defender));
    }

    private boolean finalizeMove(Context attackCtx, Context defenceCtx) {
        if (attackCtx.action == null || defenceCtx.action == null) return false;

        // 1. Изначально выполняются действия обоих бойцов
        // 2. Если фаталити, то действие защищающегося - это "ничего" (присваивается еще при атаке)
        // 3. Если действие бойца отмечается как неудачное, то оно не выполняется (провал выстрела)
        // 4. Если действие не наносит прямой урон и оно проиграло по очкам, то оно не выполняется (провал обороны)

        // TODO плавающее ограничение на получаемый урон чтобы стимулировать нокауты
        // TODO выяснить что я имел в виду под плавающим ограничением
        final boolean isFatality = defenceCtx.knockedOut;

        final ActionResult higherOne;
        final ActionResult lowerOne;
        {
            final ActionResult attack = new ActionResult(attackCtx);
            final ActionResult defence = new ActionResult(defenceCtx);
            higherOne = computeWinner(attack, defence);
            lowerOne = higherOne == attack ? defence : attack;
        }

        // 4.
        if (lowerOne.action.dealNoDamage())
            lowerOne.actionSuccessful = false;

        // 3.
        applyAction(Effect.Stage.ACTION, higherOne, lowerOne);
        applyAction(Effect.Stage.ACTION, lowerOne, higherOne);

        // Для манипуляторов уроном, например
        applyAction(Effect.Stage.AFTER_ACTION, higherOne, lowerOne);
        applyAction(Effect.Stage.AFTER_ACTION, lowerOne, higherOne);

        // Выдаем урон обоим, мало ли...
        applyDamage(lowerOne, higherOne);
        applyDamage(higherOne, lowerOne);

        // Пишем про фаталити только если оно, собственно, удачно
        final String resultMsg = isFatality && higherOne.actionSuccessful
                ? MoveFormatter.formatFatalityResult(higherOne.ctx.entity, lowerOne.ctx.entity)
                : MoveFormatter.formatActionResults(higherOne, lowerOne);

        if (attackCtx.entity instanceof EntityPlayer)
            DBHandler.INSTANCE.logMessage((EntityPlayer) attackCtx.entity, "crabs_move", resultMsg);
        if (defenceCtx.entity instanceof EntityPlayer)
            DBHandler.INSTANCE.logMessage((EntityPlayer) defenceCtx.entity, "crabs_move", resultMsg);

        MiscaUtils.notifyAround(
                attackCtx.entity, defenceCtx.entity,
                BattleDefines.NOTIFICATION_RADIUS,
                new ChatComponentText(resultMsg)
        );

        attackCtx.endEffects();
        defenceCtx.endEffects();
        attackCtx.softReset();
        defenceCtx.softReset();

        ContextManager.INSTANCE.syncContext(attackCtx);
        ContextManager.INSTANCE.syncContext(defenceCtx);

        return true;
    }

    private static void applyAction(Effect.Stage stage, ActionResult self, ActionResult target) {
        // Осторожно! Мокрый код!

        // Баффы работают всегда, но ограниченное кол-во раз
        for (final Buff b : self.ctx.buffs)
            if (b.shouldApply(stage, self, target))
                b.apply(stage, self, target);

        if (!self.actionSuccessful) return;

        // Раздача пенделей
        for (final Effect e : self.action.target_effects)
            if (!(e instanceof Buff) && e.shouldApply(stage, target, self))
                e.apply(stage, target, self);
        for (final Effect e : self.action.self_effects)
            if (!(e instanceof Buff) && e.shouldApply(stage, self, target))
                e.apply(stage, self, target);

        // Раздача баффов
        // Здесь, потому что нужно выдавать для каждого успешного действия бойца
        if (stage == Effect.Stage.ACTION) {
            for (final Effect e : self.action.target_effects)
                if (e instanceof Buff)
                    target.ctx.buffs.add((Buff) e);
            for (final Effect e : self.action.self_effects)
                if (e instanceof Buff)
                    self.ctx.buffs.add((Buff) e);
        }
    }

    private static ActionResult computeWinner(ActionResult a, ActionResult b) {
        applyAction(Effect.Stage.BEFORE_MODS, a, b);
        applyAction(Effect.Stage.BEFORE_MODS, b, a);

        do {
            a.throwDices(a.character);
            b.throwDices(b.character);

            // Для таких эффектов как ограничение на минимальные очки
            applyAction(Effect.Stage.AFTER_MODS, a, b);
            applyAction(Effect.Stage.AFTER_MODS, b, a);
        } while (a.compareTo(b) == 0);

        return a.compareTo(b) > 0 ? a : b;
    }

    private static void applyDamage(ActionResult self, ActionResult enemy) {
        if (self.damageToReceive <= 0) return;

        final boolean wasKnockedOut = self.ctx.knockedOut;
        receiveDamage(self, enemy);

        // Если нокаут появляется после применения эффектов
        if (!wasKnockedOut && self.ctx.knockedOut) {
            final String msg = MiscaUtils.l10n("misca.crabs.knocked_out", self.ctx.entity.getCommandSenderName());
            MiscaUtils.notifyAround(
                    self.ctx.entity, enemy.ctx.entity,
                    BattleDefines.NOTIFICATION_RADIUS,
                    new ChatComponentText(msg));
        }
    }

    /**
     * Тут наносится урон `себе`. Он накапливается от эффектов ранее.
     */
    private static void receiveDamage(ActionResult self, ActionResult enemy) {
        final Context selfCtx = self.ctx;
        final EntityLivingBase selfEntity = selfCtx.entity;
        final EntityLivingBase enemyEntity = enemy.ctx.entity;

        final float currentHealth = selfEntity.getHealth();
        final float armorValue = selfEntity.getTotalArmorValue();

        final float maxArmorResist = 33; // Макс. резист урона броней ~60%.
        final float armorThresholdMod = 0.3f;
        final float minArmorThreshold = 0.4f; // Урон не может быть ниже 40% резистного урона

        final float dr = (maxArmorResist - Math.min(armorValue, 20)) / maxArmorResist;
        final float damageResisted = self.damageToReceive * dr;
        final float damage = Math.round(Math.max(damageResisted - armorValue * armorThresholdMod, damageResisted * minArmorThreshold));

        final boolean isFatal = currentHealth <= damage;
        final float damageToHealth = isFatal && !selfCtx.knockedOut ? currentHealth - 1.0f : damage;

        if (isFatal) selfCtx.knockedOut = true;
        selfEntity.setHealth(currentHealth - damageToHealth);
        selfEntity.attackEntityFrom(new CrabsDamage(enemyEntity), Float.MIN_VALUE); // Нужно для визуального эффекта урона

        final float damageDealt = currentHealth - selfEntity.getHealth();
        logger.info("`{}` received {} damage from `{}`", selfEntity.getCommandSenderName(), damageDealt, enemyEntity.getCommandSenderName());
    }

    private static class Move {
        Context attacker, defender;
    }
}
