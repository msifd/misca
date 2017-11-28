package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class FighterContextMessage implements IMessage, IMessageHandler<FighterContextMessage, IMessage> {
    public FighterContext ctx;

    public FighterContextMessage(FighterContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // TODO
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // TODO
    }

    @Override
    public IMessage onMessage(FighterContextMessage message, MessageContext ctx) {
        return null;
    }
}
