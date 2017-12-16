package msifeed.mc.misca.crabs.actions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import msifeed.mc.misca.config.ConfigEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.crabs.CrabsNetwork;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ActionManager {
    INSTANCE;

    static Logger logger = LogManager.getLogger("Crabs.Actions");
    private final Map<String, Action> actions = new HashMap<>();
    private final Multimap<Action.Type, String> typeToActions = HashMultimap.create();
    private boolean shouldSync = false;

    public void onInit() {
        ConfigManager.INSTANCE.eventbus.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
        onMiscaReload(null); // Load actions
    }

    public Collection<Action> actions() {
        return actions.values();
    }

    public Action lookup(String name) {
        return actions.get(name);
    }

    public void onReceiveActionSignatures(Collection<String> signatures) {
        logger.info("Got {} Crabs actions!", signatures.size());
        typeToActions.clear();
        for (String sig : signatures) {
            final String[] parts = sig.split(":");
            final Action.Type type = Action.Type.valueOf(parts[1].toUpperCase());
            typeToActions.put(type, parts[0]);
        }
    }

    @Subscribe
    public void onMiscaReload(ConfigEvent.ReloadDone event) {
        final List<Action> newActions = ActionProvider.INSTANCE.load();
        if (newActions.isEmpty()) return;
        actions.clear();
        for (Action a : newActions) actions.put(a.name, a);

        shouldSync = true;
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            final ActionListMessage msg = new ActionListMessage(actions.values());
            CrabsNetwork.INSTANCE.sendToPlayer((EntityPlayerMP) event.player, msg);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (!shouldSync) return;
        // Sync players
        shouldSync = false;
        CrabsNetwork.INSTANCE.sendToAll(new ActionListMessage(actions.values()));
    }
}
