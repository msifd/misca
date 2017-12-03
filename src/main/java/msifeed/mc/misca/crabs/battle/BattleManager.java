package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import msifeed.mc.misca.crabs.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
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

    public boolean isBattling(UUID uuid) {
        return uuidToContext.containsKey(uuid);
    }

    public boolean isBattling(Entity entity) {
        return uuidToContext.containsKey(EntityUtils.getUuid(entity));
    }

    public FighterContext getContext(UUID uuid) {
        return uuidToContext.get(uuid);
    }

    public FighterContext getContext(Entity entity) {
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

    public void leaveBattle(UUID uuid) {
        FighterContext ctx = uuidToContext.get(uuid);
        if (ctx == null) return;

        ctx.updateState(FighterContext.State.LEAVING);
        // Энтити выходят из боя мгновенно
        if (!(ctx.entity instanceof EntityPlayer)) {
            ctx.lastStateChange = 0;
            uuidToContext.remove(uuid);
        }
        toSync.add(ctx);

        logger.info("{} is leaving the battle.", ctx.entity.getCommandSenderName());
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

        if (ctx.state != FighterContext.State.LEAVING && action.type == FighterAction.Type.LEAVE) {
            leaveBattle(ctx.uuid);
        }
    }

    void onUpdateFromServer(ArrayList<FighterContext> vctx) {
        for (FighterContext ctx : vctx) {
            if (ctx.isWaitedForLeave()) {
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
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        FighterContext ctx = uuidToContext.get(event.player.getUniqueID());
        if (ctx == null) return;

        if (ctx.isWaitedForLeave()) {
            uuidToContext.remove(ctx.uuid);
            toSync.add(ctx);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDamage(LivingAttackEvent event) {
        if (!(event.entity instanceof EntityPlayer) || !(event.source instanceof EntityDamageSource)) return;

        // Ограничиваем получение урона бойцами
        EntityDamageSource source = (EntityDamageSource) event.source;
        FighterContext attacker_ctx = getContext(source.getEntity());
        FighterContext target_ctx = getContext(event.entity);
        if (attacker_ctx != null && !attacker_ctx.canAttack() || attacker_ctx == null && target_ctx != null)
            event.setCanceled(true);

        // TODO обрабатываем урон как ход
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        FighterContext context = uuidToContext.get(event.player.getUniqueID());
        if (context == null) return;

        context.entity = event.player;

        if (event.player instanceof EntityPlayerMP)
            BattleNetwork.INSTANCE.syncPlayer((EntityPlayerMP) event.player, uuidToContext.values());
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        FighterContext ctx = uuidToContext.get(event.player.getUniqueID());
        if (ctx == null) return;

        // Сохраняем контекст 10 минут
        final int KEEP_CTX_TIME = 600;
        ctx.updateState(FighterContext.State.LEAVING);
        ctx.lastStateChange -= KEEP_CTX_TIME;
        toSync.add(ctx);
    }
}
