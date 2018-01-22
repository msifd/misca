package msifeed.mc.misca.tweaks.entity_control;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.config.ConfigEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.tweaks.Tweaks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EntityControl {
    private static final Type entryListType = new TypeToken<List<ControlEntry>>() {
    }.getType();
    private static Gson gson = new GsonBuilder().registerTypeAdapter(entryListType, new EntityControlSerializer()).create();
    private File configFile;

    private ArrayList<ControlEntry> rules = new ArrayList<>();

    public EntityControl() {
        ConfigManager.INSTANCE.eventbus.register(this);
    }

    @Subscribe
    public void onReloadDone(ConfigEvent.ReloadDone event) {
        configFile = new File(ConfigManager.config_dir, "entity_control.json");

        if (!configFile.exists()) {
            try {
                Files.write(configFile.toPath(), "[ ]".getBytes(UTF_8), StandardOpenOption.CREATE_NEW);
                Tweaks.logger.info("[EntityControl] Create empty config and skip.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        Tweaks.logger.info("[EntityControl] Loading banned entities...");
        try {
            final String json = new String(Files.readAllBytes(configFile.toPath()), UTF_8);
            rules = gson.fromJson(json, entryListType);
        } catch (Exception e) {
            Tweaks.logger.error("[EntityControl] Failed to load entities! Cause: `{}`", e.getMessage());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoin(EntityJoinWorldEvent event) {
        final Class ec = event.entity.getClass();
        if (!EntityLivingBase.class.isAssignableFrom(ec)) return;

        final String world = event.world.getWorldInfo().getWorldName();
        for (ControlEntry entry : rules) {
            if (entry.aClass.isAssignableFrom(ec)) {
                if (entry.dimensions != null) {
                    if (entry.dimensions.contains(world)) {
                        event.setCanceled(true);
                        return;
                    }
                } else {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    static class ControlEntry {
        public Class aClass;
        public Set<String> dimensions;
    }
}
