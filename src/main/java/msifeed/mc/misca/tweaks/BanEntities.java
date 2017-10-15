package msifeed.mc.misca.tweaks;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.config.ConfigEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.config.TomlConfig;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class BanEntities {
    private TomlConfig<ConfigSection> config = new TomlConfig<>(ConfigSection.class, "banned_entities.toml");
    private HashSet<Class> classesSet = new HashSet<>();

    public BanEntities() {
        config.setServerOnly(true);

        // Remake classes set
        ConfigManager.eventbus.register(this);
    }

    @Subscribe
    public void onReloadDone(ConfigEvent.ReloadDone event) {
        classesSet.clear();
        for (String cn : config.get().classes) {
            try {
                classesSet.add(Class.forName(cn));
            } catch (ClassNotFoundException e) {
                Tweaks.logger.warn("[BanEntities] Entity class not found: {}", cn);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (classesSet.contains(event.entity.getClass()))
            event.setCanceled(true);
    }

    static class ConfigSection implements Serializable {
        ArrayList<String> classes = new ArrayList<>();
    }
}
