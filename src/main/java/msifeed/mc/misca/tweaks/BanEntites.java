package msifeed.mc.misca.tweaks;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.config.TomlConfig;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.io.Serializable;
import java.util.ArrayList;

public class BanEntites {
    private TomlConfig<ConfigSection> config = new TomlConfig<>(ConfigSection.class, "banned_entities.toml");

    public BanEntites() {
        config.setServerOnly(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (config.get().classes.contains(event.entity.getClass().getName()))
            event.setCanceled(true);
    }

    static class ConfigSection implements Serializable {
        ArrayList<String> classes = new ArrayList<>();
    }
}
