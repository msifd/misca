package msifeed.misca.combat;

import com.google.gson.reflect.TypeToken;
import msifeed.misca.Misca;
import msifeed.misca.combat.battle.BattleManager;
import msifeed.misca.combat.battle.BattleStateSync;
import msifeed.misca.combat.cap.*;
import msifeed.misca.combat.client.CombatTheme;
import msifeed.misca.combat.rules.Rules;
import msifeed.misca.combat.rules.WeaponInfo;
import msifeed.sys.sync.SyncChannel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Combat {
    public static final BattleManager MANAGER = new BattleManager();

    public static final SyncChannel<Rules> RULES
            = new SyncChannel<>(Misca.RPC, Paths.get(Misca.MODID, "combat_rules.json"), TypeToken.get(Rules.class));
    public static final TypeToken<HashMap<ResourceLocation, WeaponInfo>> WPN_TT = new TypeToken<HashMap<ResourceLocation, WeaponInfo>>() {};
    public static final SyncChannel<HashMap<ResourceLocation, WeaponInfo>> WEAPONS
            = new SyncChannel<>(Misca.RPC, Paths.get(Misca.MODID, "combat_weapons.json"), WPN_TT);

    public static Rules getRules() {
        return RULES.get();
    }

    public static Map<ResourceLocation, WeaponInfo> getWeapons() {
        return WEAPONS.get();
    }

    public static void sync() throws Exception {
        RULES.sync();
        WEAPONS.sync();
    }

    public void preInit() {
        CapabilityManager.INSTANCE.register(ICombatant.class, new CombatantStorage(), Combatant::new);
        MinecraftForge.EVENT_BUS.register(new CombatantEventHandler());
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(MANAGER);
        MinecraftForge.EVENT_BUS.register(new CombatHandler());

        if (FMLCommonHandler.instance().getSide().isClient()) {
            CombatTheme.load();
            Misca.RPC.register(new CombatantSync());
            Misca.RPC.register(new BattleStateSync());
        }
    }

    public static Optional<WeaponInfo> getWeaponInfo(Item item) {
        final ResourceLocation handLoc = item.getRegistryName();
        return Optional.ofNullable(Combat.getWeapons().get(handLoc));
    }

    public static Optional<WeaponInfo> getWeaponInfo(EntityLivingBase entity, EnumHand hand) {
        final ResourceLocation handLoc = entity.getHeldItem(hand).getItem().getRegistryName();
        return Optional.ofNullable(Combat.getWeapons().get(handLoc));
    }
}
