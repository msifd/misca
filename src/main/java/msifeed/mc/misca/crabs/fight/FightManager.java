package msifeed.mc.misca.crabs.fight;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.crabs.CrabsNetwork;
import msifeed.mc.misca.crabs.context.Context;
import msifeed.mc.misca.crabs.context.ContextManager;
import msifeed.mc.misca.crabs.context.ContextMessage;
import msifeed.mc.misca.crabs.rules.Rules;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum FightManager {
    INSTANCE;

    private Logger logger = LogManager.getLogger("Crabs.Fight");

    public void onInit() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    public void joinFight(Context context) {
        if (context.status.isFighting()) return;

        context.status = Context.Status.ACTIVE;
        ContextManager.INSTANCE.syncContext(context);

        logger.info("{} is joined fight", context.entity.getCommandSenderName());
    }

    public void leaveFight(Context context, boolean instant) {
        // Энтити выходят из боя мгновенно
        if (instant || !(context.entity instanceof EntityPlayer)) {
            removeFromFight(context);
            return;
        }

        if (context.status == Context.Status.LEAVING) {
            // Отмена выхода из боя
            context.updateStatus(Context.Status.ACTIVE);
            logger.info("{} is not leaving fight anymore", context.entity.getCommandSenderName());
        } else {
            context.updateStatus(Context.Status.LEAVING);
            logger.info("{} is leaving fight", context.entity.getCommandSenderName());
        }

        ContextManager.INSTANCE.syncContext(context);
    }

    private void removeFromFight(Context context) {
        context.updateStatus(Context.Status.NEUTRAL);
        ContextManager.INSTANCE.unbindTargetToContext(context);
        ContextManager.INSTANCE.syncContext(context);
        logger.info("{} removed from the fight", context.entity.getCommandSenderName());
    }

    public void toggleControl(Context controller, Context actor) {
        // Игроков нельзя контролить, да?
        if (actor.entity instanceof EntityPlayer || !controller.status.isFighting()) return;

        // Выключаем контроль при повторном вызове или устанавливаем новую марионетку
        final boolean controlTaken = controller.puppet == null || !controller.puppet.equals(actor.uuid);
        controller.puppet = controlTaken ? actor.uuid : null;

        if (controller.entity instanceof EntityPlayerMP) {
            final ContextMessage msg = new ContextMessage(ImmutableList.of(controller, actor));
            CrabsNetwork.INSTANCE.sendToPlayer((EntityPlayerMP) controller.entity, msg);
        }

        ContextManager.INSTANCE.syncContext(controller);

        if (controlTaken)
            logger.info("`{}` took control over `{}`", controller.entity.getCommandSenderName(), actor.entity.getCommandSenderName());
        else
            logger.info("`{}` released control", controller.entity.getCommandSenderName());
    }

    void onMessageFromClient(EntityPlayerMP player, FighterMessage message) {
        final Context context = ContextManager.INSTANCE.getContext(player.getUniqueID());

        switch (message.type) {
            case JOIN:
                joinFight(context);
                break;
            case LEAVE:
                leaveFight(context, false);
                break;
            case ACTION:
                final Context actor = context == null || context.puppet == null ? context : ContextManager.INSTANCE.getContext(context.puppet);
                if (actor != null)
                    MoveManager.INSTANCE.selectAction(actor, message.action, message.mod);
                break;
            case CALC:
                Rules.rollSingleStat(player, context, message.stat, message.mod);
                break;
        }
    }

    // TODO добавить паузы между проверками
    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        final Context ctx = ContextManager.INSTANCE.getContext(event.entityLiving);
        if (ctx == null) return;

        // Восстанавливаем "сознание" если подлечились до четверти здоровья
        if (ctx.knockedOut && ctx.entity != null && ctx.entity.getHealth() >= (ctx.entity.getMaxHealth() / 4f)) {
            ctx.knockedOut = false;
            ContextManager.INSTANCE.syncContext(ctx);
        }

        final long now = System.currentTimeMillis() / 1000;
        final long statusAge = now - ctx.lastStatusChange;

        switch (ctx.status) {
            case LEAVING:
                if (statusAge >= BattleDefines.SECS_BEFORE_LEAVE_BATTLE) removeFromFight(ctx);
                break;
            case DEAL_DAMAGE:
                if (statusAge >= BattleDefines.SECS_TO_DEAL_DAMAGE) MoveManager.INSTANCE.stopDealingDamage(ctx);
                break;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamage(LivingHurtEvent event) {
        if (!(event.source instanceof EntityDamageSource)) return;

        final EntityDamageSource damage = (EntityDamageSource) event.source;

        // Пришел урон по результатам хода
        if (damage instanceof CrabsDamage) return;

        final Context attacker = ContextManager.INSTANCE.getContext((EntityLivingBase) damage.getEntity());
        final Context target = ContextManager.INSTANCE.getContext(event.entityLiving);
        final boolean attackerFighting = attacker != null && attacker.status.isFighting();
        final boolean targetFighting = target != null && target.status.isFighting();

        // Механ, если оба не сражаются через крабс
        if (!attackerFighting && !targetFighting) return;

        // Бить бойцов покидающих бой можно!
        if (target != null && target.status == Context.Status.LEAVING) return;

        // Отменяем получение урона. Его мы вернем после хода... может быть
        event.setCanceled(true);

        // Если в бою только одна сторона, то урон не возвращаем
        if (attackerFighting != targetFighting) return;

        // Хотя бить может и закулисье, правила выдачи урона считаются для марионетки
        final Context actor = attacker.puppet == null ? attacker : ContextManager.INSTANCE.getContext(attacker.puppet);

        // Мешаем марионетке избивать саму себя
        if (actor == target) return;

        // Если атакующий еще не должен бить (завершать ход), то игнорим
        if (!actor.canAttack()) return;

        // После того как цель определена можно атаковать только её
        if (actor.target != null && !actor.target.equals(target.uuid)) return;

        MoveManager.INSTANCE.dealDamage(actor, target, damage, event.ammount);
    }

    @SubscribeEvent
    public void onChatMessage(ServerChatEvent event) {
        if (event.message.trim().isEmpty()) return;

        final Context ctx = ContextManager.INSTANCE.getContext(event.player.getUniqueID());
        if (ctx == null) return;

//        final int words = event.message.split("\\s+").length;
//        if (words < BattleDefines.MIN_WORDS_IN_ACTION_DESC) return;

        final Context actor = ctx.puppet == null ? ctx : ContextManager.INSTANCE.getContext(ctx.puppet);
        MoveManager.INSTANCE.describeAction(actor);
        ContextManager.INSTANCE.syncContext(actor);
    }
}
