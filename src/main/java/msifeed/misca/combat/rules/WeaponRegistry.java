package msifeed.misca.combat.rules;

import com.google.gson.reflect.TypeToken;
import msifeed.misca.Misca;
import msifeed.sys.sync.SyncChannel;
import net.minecraft.util.ResourceLocation;

import java.nio.file.Paths;
import java.util.HashMap;

public class WeaponRegistry {
    public static final TypeToken<HashMap<ResourceLocation, WeaponInfo>> WPN_TT = new TypeToken<HashMap<ResourceLocation, WeaponInfo>>() {};

    public static final SyncChannel<HashMap<ResourceLocation, WeaponInfo>> WEAPONS
            = new SyncChannel<>(Misca.RPC, Paths.get(Misca.MODID, "combat_weapons.json"), WPN_TT);

    public static void sync() throws Exception {
        WEAPONS.sync();
    }

    public static WeaponInfo getWeaponInfo(ResourceLocation key) {
        return WEAPONS.get().getOrDefault(key, WeaponInfo.NONE);
    }
}
