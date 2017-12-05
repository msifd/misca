package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import msifeed.mc.misca.crabs.EntityUtils;
import msifeed.mc.misca.crabs.actions.ActionManager;
import msifeed.mc.misca.crabs.actions.Actions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public enum BattleManager {
    INSTANCE;

    private Logger logger = LogManager.getLogger("Crabs.Battle");
    private HashMap<UUID, FighterContext> uuidToContext = new HashMap<>();

    private long lastSync = 0;
    private ArrayList<FighterContext> toSync = new ArrayList<>();

    public void onInit(FMLInitializationEvent event) {
        BattleNetwork.INSTANCE.onInit(event);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    public boolean isBattling(EntityLivingBase entity) {
        return uuidToContext.containsKey(EntityUtils.getUuid(entity));
    }

    public FighterContext getContext(UUID uuid) {
        return uuidToContext.get(uuid);
    }

    public FighterContext getContext(EntityLivingBase entity) {
        return uuidToContext.get(EntityUtils.getUuid(entity));
    }

    public Collection<FighterContext> getContexts() {
        return uuidToContext.values();
    }

    public void joinBattle(EntityLivingBase entity) {
        if (isBattling(entity)) return;

        FighterContext ctx = new FighterContext(entity);
        uuidToContext.put(ctx.uuid, ctx);
        toSync.add(ctx);

        logger.info("{} is joined the battle.", ctx.entity.getCommandSenderName());
    }

    public void toggleControl(EntityPlayer player, EntityLivingBase entity) {
        // Игроков нельзя контролить, да?
        if (entity instanceof EntityPlayer) return;

        final FighterContext controller = uuidToContext.get(player.getUniqueID());
        final FighterContext actor = getContext(entity);
        if (controller == null || actor == null) return;

        // Выключаем контроль при повторном вызове или устанавливаем новую марионетку
         controller.control = (controller.control == actor.uuid) ? null : actor.uuid;
    }

    public void leaveBattle(EntityLivingBase entity, boolean instant) {
        FighterContext ctx = uuidToContext.get(EntityUtils.getUuid(entity));
        if (ctx == null) return;

        // Энтити выходят из боя мгновенно
        if (instant || !(ctx.entity instanceof EntityPlayer)) {
            removeFromBattle(ctx);
        }
        else {
            ctx.updateStatus(FighterContext.Status.LEAVING);
            toSync.add(ctx);
            logger.info("{} is leaving the battle.", ctx.entity.getCommandSenderName());
        }
    }

    private void removeFromBattle(FighterContext ctx) {
        ctx.updateStatus(FighterContext.Status.LEAVING);
        ctx.lastStateChange = 0;
        uuidToContext.remove(ctx.uuid);

        if (ctx.target != null) {
            // TODO unbind targets, controlls
        }

        toSync.add(ctx);
        logger.info("{} remove from the battle.", ctx.entity.getCommandSenderName());
    }

    private void syncBattleList() {
        long now = System.currentTimeMillis();
        if (now - lastSync < 500 || toSync.isEmpty()) return;
        lastSync = now;

        BattleNetwork.INSTANCE.syncAll(toSync);
        toSync.clear();
    }

    void onActionFromClient(EntityPlayerMP player, FighterAction action) {
        FighterContext ctx = uuidToContext.get(player.getUniqueID());

        if (ctx == null) {
            if (action.type == FighterAction.Type.JOIN) joinBattle(player);
            return;
        }

        FighterContext actor = ctx.control == null ? ctx : getContext(ctx.control);

        switch (action.type) {
            case MOVE:
                actor.updateAction(Actions.point_hit);
//                FighterContext target = getContext(actor.target);
//                ActionManager.INSTANCE.doAction(actor, target);
                break;
            case LEAVE:
                if (ctx.status != FighterContext.Status.LEAVING) leaveBattle(player, false);
                break;
        }
    }

    void onUpdateFromServer(ArrayList<FighterContext> vctx) {
        for (FighterContext ctx : vctx) {
            if (ctx.canLeaveNow()) {
                uuidToContext.remove(ctx.uuid);
                continue;
            }

            FighterContext oldCtx = uuidToContext.get(ctx.uuid);
            if (oldCtx != null && oldCtx.entity != null) ctx.entity = oldCtx.entity;
            else ctx.entity = EntityUtils.lookupPlayer(ctx.uuid);

            uuidToContext.put(ctx.uuid, ctx);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        syncBattleList();
    }

    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        final FighterContext ctx = getContext(event.entityLiving);
        if (ctx == null) return;

        if (ctx.canLeaveNow()) removeFromBattle(ctx);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamage(LivingAttackEvent event) {
        if (!(event.entity instanceof EntityPlayer) || !(event.source instanceof EntityDamageSource)) return;

        final EntityDamageSource damage = (EntityDamageSource) event.source;
        final FighterContext attacker_ctx = getContext((EntityLivingBase) damage.getEntity());
        final FighterContext target_ctx = getContext(event.entityLiving);

        // Урон проходит как обычно только когда никто не дерется
        if (attacker_ctx == null && target_ctx == null) return;

        // Отменяем получение урона. Его мы вернем после хода... может быть
        event.setCanceled(true);

        // Если в бою только одна сторона, то урон не возвращаем
        if (attacker_ctx == null || target_ctx == null) return;

        // Если атакующий еще не должен бить (завершать ход), то игнорим
        if (!attacker_ctx.canAttack()) return;

        FighterContext actor = attacker_ctx.control == null ? attacker_ctx : getContext(attacker_ctx.control);
        ActionManager.INSTANCE.passDamage(actor, damage);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        final FighterContext context = uuidToContext.get(event.player.getUniqueID());
        if (context == null) return;

        context.entity = event.player;

        if (event.player instanceof EntityPlayerMP)
            BattleNetwork.INSTANCE.syncPlayer((EntityPlayerMP) event.player, uuidToContext.values());
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        final FighterContext ctx = uuidToContext.get(event.player.getUniqueID());
        if (ctx == null) return;

        // Сохраняем контекст 10 минут
        final int KEEP_CTX_TIME = 600;
        ctx.updateStatus(FighterContext.Status.LEAVING);
        ctx.lastStateChange -= KEEP_CTX_TIME;
        toSync.add(ctx);
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        final FighterContext ctx = getContext(event.entityLiving);
        if (ctx == null) return;

        // TODO unbind targets
        leaveBattle(event.entityLiving, true);
    }

    @SubscribeEvent
    public void onChatMessage(ServerChatEvent event) {
        final FighterContext ctx = uuidToContext.get(event.player.getUniqueID());
        if (ctx == null) return;

        // Удаляем все что не буква, цифра или пробел
        final String cleanedMsg = event.message.replaceAll("[^\\w\\d\\s]", "");
        final int words = cleanedMsg.split("\\s+").length;
        if (words < BattleDefines.MIN_WORDS_IN_MOVE_DESC) return;

        ctx.described = true;
    }
}
