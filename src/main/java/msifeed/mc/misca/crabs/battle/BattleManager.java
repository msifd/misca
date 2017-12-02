package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.misca.crabs.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
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

    public FighterContext getContext(UUID uuid) {
        return uuidToContext.get(uuid);
    }

    public void joinBattle(EntityPlayer player) {
        if (uuidToContext.containsKey(player.getUniqueID())) return;

        FighterContext ctx = new FighterContext(player);
        uuidToContext.put(ctx.uuid, ctx);
        toSync.add(ctx);

        logger.info("{} is joined battle.", ctx.player.getDisplayName());
    }

    public void leaveBattle(UUID uuid) {
        FighterContext ctx = uuidToContext.get(uuid);
        if (ctx == null) return;

        ctx.updateState(FighterContext.State.LEAVING);
        toSync.add(ctx);

        logger.info("{} is leaving battle.", ctx.player.getDisplayName());
    }

    @SideOnly(Side.SERVER)
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

    @SideOnly(Side.CLIENT)
    void onUpdateFromServer(ArrayList<FighterContext> vctx) {
        for (FighterContext ctx : vctx) {
            if (ctx.isWaitedForLeave()) uuidToContext.remove(ctx.uuid);
            else {
                ctx.player = EntityUtils.lookupPlayer(ctx.uuid);
                uuidToContext.put(ctx.uuid, ctx);
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        long now = System.currentTimeMillis();
        if (now - lastSync < 500 || toSync.isEmpty()) return;
        lastSync = now;

        BattleNetwork.INSTANCE.syncAll(toSync);
        toSync.clear();
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
        EntityDamageSource source = (EntityDamageSource) event.source;
        FighterContext attacker_ctx = uuidToContext.get(source.getEntity().getUniqueID());
        FighterContext target_ctx = uuidToContext.get(event.entity.getUniqueID());
        if (attacker_ctx != null && !attacker_ctx.canAttack() || attacker_ctx == null && target_ctx != null)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.entity;
        FighterContext context = uuidToContext.get(player.getUniqueID());
        if (context == null) return;

        context.player = player;

        if (player instanceof EntityPlayerMP)
            BattleNetwork.INSTANCE.syncPlayer((EntityPlayerMP) player, uuidToContext.values());
    }
}
