package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public enum BattleManager {
    INSTANCE;

    private static final int LEAVING_WAIT = 5; // in secs

    private Logger logger = LogManager.getLogger("Crabs.Battle");
    private HashMap<UUID, FighterContext> uuidToContext = new HashMap<>();

    public boolean isBattling(UUID uuid) {
        return uuidToContext.containsKey(uuid);
    }

    public FighterContext getContext(UUID uuid) {
        return uuidToContext.get(uuid);
    }

    public FighterContext joinBattle(EntityPlayer player) {
        FighterContext context = new FighterContext(player);
        uuidToContext.put(player.getUniqueID(), context);
        return context;
    }

    public void leaveBattle(UUID uuid) {
        FighterContext context = uuidToContext.get(uuid);
        if (context == null) return;
        context.updateState(FighterContext.State.LEAVING);
//        return uuidToContext.remove(uuid);
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (event.phase != TickEvent.Phase.END || (event.type != TickEvent.Type.SERVER && event.type != TickEvent.Type.CLIENT)) {
            return;
        }

        long now = System.currentTimeMillis() / 1000;
        uuidToContext.values().stream()
                .filter(c -> c.state == FighterContext.State.LEAVING && (now - c.lastStateChange > LEAVING_WAIT))
                .collect(Collectors.toList())
                .forEach(c -> uuidToContext.remove(c.uuid));
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.entity;
        FighterContext context = uuidToContext.get(player.getUniqueID());
        if (context == null) return;

        context.player = player;

        if (player instanceof EntityPlayerMP)
            BattleNetwork.INSTANCE.notifyContext((EntityPlayerMP) player, context);
    }
}
