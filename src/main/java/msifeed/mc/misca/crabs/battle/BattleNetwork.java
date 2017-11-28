package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;

public enum BattleNetwork {
    INSTANCE;

    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("crabs.battle");

    public void notifyContext(EntityPlayerMP playerMP, FighterContext ctx) {
        network.sendTo(new FighterContextMessage(ctx), playerMP);
    }
}
