package msifeed.misca.combat;

import msifeed.misca.Misca;
import msifeed.misca.combat.battle.BattleManager;
import msifeed.misca.combat.battle.BattleStateSync;
import msifeed.misca.combat.cap.*;
import msifeed.misca.combat.client.CombatTheme;
import msifeed.misca.combat.rules.Rules;
import msifeed.sys.sync.SyncChannel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.nio.file.Paths;

public class Combat {
    public static final BattleManager MANAGER = new BattleManager();
    public static final SyncChannel<CombatConfig> CONFIG
            = new SyncChannel<>(Misca.RPC, Paths.get(Misca.MODID, "combat.json"), CombatConfig.class);

    public static CombatConfig getConfig() {
        return CONFIG.get();
    }
    public static Rules getRules() {
        return CONFIG.get().rules;
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
}
