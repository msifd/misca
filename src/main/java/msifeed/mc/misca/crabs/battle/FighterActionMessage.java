package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class FighterActionMessage implements IMessage, IMessageHandler<FighterActionMessage, IMessage> {
    FighterAction action;

    public FighterActionMessage() {
    }

    FighterActionMessage(FighterAction action) {
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        FighterAction.Type type = FighterAction.Type.values()[buf.readByte()];
        this.action = new FighterAction(type);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(action.type.ordinal());
    }

    @Override
    public IMessage onMessage(FighterActionMessage message, MessageContext ctx) {
        BattleManager.INSTANCE.onActionFromClient(ctx.getServerHandler().playerEntity, message.action);
        return null;
    }
}
