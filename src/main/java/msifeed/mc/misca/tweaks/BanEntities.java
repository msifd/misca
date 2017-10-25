package msifeed.mc.misca.tweaks;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.config.ConfigEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.config.JsonConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class BanEntities {
    private JsonConfig<ConfigSection> config = ConfigManager.getServerConfigFor(ConfigSection.class, "banned_entities.json");
    private HashSet<Class> classesSet = new HashSet<>();

    public BanEntities() {
        // Remake classes set
        ConfigManager.INSTANCE.eventbus.register(this);
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
        Class ec = event.entity.getClass();
        if (!EntityLivingBase.class.isAssignableFrom(ec))
            return;
        for (Class c : classesSet) {
            if (c.isAssignableFrom(ec)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    static class ConfigSection implements Serializable {
        ArrayList<String> classes = new ArrayList<>();
    }
}
