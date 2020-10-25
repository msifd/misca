package msifeed.misca.combat;

import msifeed.misca.Misca;
import msifeed.misca.combat.battle.BattleManager;
import msifeed.misca.combat.rpc.CombatServerRpc;
import net.minecraftforge.common.MinecraftForge;

public class Combat {
    private final BattleManager battleManager = new BattleManager();

    public void init() {
        MinecraftForge.EVENT_BUS.register(battleManager);
        MinecraftForge.EVENT_BUS.register(new DamageHandler(battleManager));
        Misca.RPC.register(new CombatServerRpc(battleManager));
    }
}
