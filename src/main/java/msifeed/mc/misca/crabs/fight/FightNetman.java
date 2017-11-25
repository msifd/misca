package msifeed.mc.misca.crabs.fight;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

import msifeed.mc.misca.crabs.fight.FightNetman.FightMessage;

public enum FightNetman implements IMessageHandler<FightMessage, FightMessage> {
    INSTANCE;

    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("crabs.fight");

    public void init(FMLPreInitializationEvent event) {
        network.registerMessage(INSTANCE, FightMessage.class, 0, Side.CLIENT);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    void notifyFightMembers(Fight fight, Scope scope) {
        FightMessage msg = new FightMessage();
        msg.scope = scope;

    }

    @Override
    public FightMessage onMessage(FightMessage message, MessageContext ctx) {
        return null;
    }

    public enum Scope {
        ALL, MEMBERS, MOVE
    }

    public class FightMessage implements IMessage {
        Scope scope;


        @Override
        public void fromBytes(ByteBuf buf) {

        }

        @Override
        public void toBytes(ByteBuf buf) {

        }
    }
}
