package msifeed.misca.combat;

import com.google.gson.reflect.TypeToken;
import msifeed.misca.Misca;
import msifeed.misca.combat.battle.BattleManager;
import msifeed.misca.combat.battle.BattleStateSync;
import msifeed.misca.combat.cap.*;
import msifeed.misca.combat.client.CombatTheme;
import msifeed.misca.combat.rules.Rules;
import msifeed.misca.combat.rules.WeaponRegistry;
import msifeed.sys.sync.SyncChannel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Combat {
    public static final SyncChannel<Rules> RULES
            = new SyncChannel<>(Misca.RPC, "combat_rules.json", TypeToken.get(Rules.class));
    public static final SyncChannel<WeaponRegistry> WEAPONS = new SyncChannel<>(
            Misca.RPC, "combat_weapons.json", TypeToken.get(WeaponRegistry.class));

    public static Rules getRules() {
        return RULES.get();
    }

    public static WeaponRegistry getWeapons() {
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
        MinecraftForge.EVENT_BUS.register(BattleManager.class);
        MinecraftForge.EVENT_BUS.register(new CombatHandler());

        Misca.RPC.register(new CombatantSync());

        if (FMLCommonHandler.instance().getSide().isClient()) {
            CombatTheme.load();
            Misca.RPC.register(new BattleStateSync());
            Misca.RPC.register(new CombatEventSync());
        }
    }
}
