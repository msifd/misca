package msifeed.mc.misca.crabs.actions;

import com.google.common.eventbus.Subscribe;
import msifeed.mc.misca.config.ConfigEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.crabs.actions.Action.Type;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Effect.Damage;
import msifeed.mc.misca.crabs.rules.Effect.Fire;
import msifeed.mc.misca.crabs.rules.Modifier;
import msifeed.mc.misca.crabs.rules.Modifier.DiceG30Plus;
import msifeed.mc.misca.crabs.rules.Modifier.Stat;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public enum Actions {
    INSTANCE;

    private final HashMap<String, Action> actions = new HashMap<>();

    public void onInit() {
        ConfigManager.INSTANCE.eventbus.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        onMiscaReload(null); // Load actions
    }

    public Collection<Action> all() {
        return actions.values();
    }

    public Action lookup(String name) {
        return actions.get(name);
    }

    @Subscribe
    public void onMiscaReload(ConfigEvent.ReloadDone event) {
        final List<Action> newActions = ActionProvider.INSTANCE.load();
        if (newActions.isEmpty()) return;
        actions.clear();
        for (Action a : newActions) actions.put(a.name, a);
    }
}
