package msifeed.misca.combat;

import msifeed.misca.Misca;
import msifeed.misca.combat.battle.BattleManager;
import msifeed.misca.combat.cap.*;
import msifeed.misca.combat.rpc.CombatServerRpc;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Combat {
    public static final BattleManager MANAGER = new BattleManager();

    public void preInit() {
        CapabilityManager.INSTANCE.register(ICombatant.class, new CombatantStorage(), Combatant::new);
        MinecraftForge.EVENT_BUS.register(new CombatantEventHandler());
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(MANAGER);
        MinecraftForge.EVENT_BUS.register(new CombatHandler());

        Misca.RPC.register(new CombatServerRpc(MANAGER));

        if (FMLCommonHandler.instance().getSide().isClient()) {
            Misca.RPC.register(new CombatantSync());
            Misca.RPC.register(new CombatServerRpc(MANAGER));
        }
    }
}
