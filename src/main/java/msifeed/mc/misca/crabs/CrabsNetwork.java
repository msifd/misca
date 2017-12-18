package msifeed.mc.misca.crabs;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import msifeed.mc.misca.crabs.actions.ActionListMessage;
import msifeed.mc.misca.crabs.battle.FighterContextMessage;
import msifeed.mc.misca.crabs.battle.FighterMessage;
import msifeed.mc.misca.crabs.character.CharacterMessage;
import net.minecraft.entity.player.EntityPlayerMP;

public enum CrabsNetwork {
    INSTANCE;

    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.crabs");

    public void onInit() {
        network.registerMessage(CharacterMessage.class, CharacterMessage.class, 0x00, Side.SERVER);
        network.registerMessage(CharacterMessage.class, CharacterMessage.class, 0x01, Side.CLIENT);
        network.registerMessage(FighterMessage.class, FighterMessage.class, 0x10, Side.SERVER);
        network.registerMessage(FighterContextMessage.class, FighterContextMessage.class, 0x11, Side.CLIENT);
        network.registerMessage(ActionListMessage.class, ActionListMessage.class, 0x21, Side.CLIENT);
    }

    public void sendToPlayer(EntityPlayerMP playerMP, IMessage message) {
        network.sendTo(message, playerMP);
    }

    public void sendToAll(IMessage message) {
        network.sendToAll(message);
    }

    public void sendToServer(IMessage message) {
        network.sendToServer(message);
    }
}
