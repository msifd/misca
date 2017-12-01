package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Collection;

public enum BattleNetwork {
    INSTANCE;

    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("crabs.battle");

    public void onInit(FMLInitializationEvent event) {
        network.registerMessage(FighterContextMessage.class, FighterContextMessage.class, 0, Side.CLIENT);
    }

    public void syncPlayer(EntityPlayerMP playerMP, Collection<FighterContext> toSync) {
        network.sendTo(new FighterContextMessage(toSync), playerMP);
    }

    public void syncAll(Collection<FighterContext> toSync) {
        network.sendToAll(new FighterContextMessage(toSync));
    }
}
