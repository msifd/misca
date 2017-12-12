package msifeed.mc.misca.crabs;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.misca.crabs.battle.FighterContext;
import msifeed.mc.misca.crabs.battle.FighterContextMessage;
import msifeed.mc.misca.crabs.battle.FighterMessage;
import msifeed.mc.misca.crabs.character.CharacterMessage;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Collection;

public enum CrabsNetwork {
    INSTANCE;

    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.crabs");

    public void onInit() {
        network.registerMessage(CharacterMessage.class, CharacterMessage.class, 1, Side.SERVER);
        network.registerMessage(FighterMessage.class, FighterMessage.class, 2, Side.SERVER);
        network.registerMessage(FighterContextMessage.class, FighterContextMessage.class, 3, Side.CLIENT);
    }

    public void syncPlayer(EntityPlayerMP playerMP, Collection<FighterContext> toSync) {
        network.sendTo(new FighterContextMessage(toSync), playerMP);
    }

    public void syncAll(Collection<FighterContext> toSync) {
        network.sendToAll(new FighterContextMessage(toSync));
    }

    @SideOnly(Side.CLIENT)
    public void notifyServer(IMessage message) {
        network.sendToServer(message);
    }
}
