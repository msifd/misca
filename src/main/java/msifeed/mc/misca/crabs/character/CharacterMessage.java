package msifeed.mc.misca.crabs.character;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class CharacterMessage implements IMessage, IMessageHandler<CharacterMessage, IMessage> {
    byte[] stats = new byte[Stats.values().length];

    public CharacterMessage() {
    }

    public CharacterMessage(byte[] stats) {
        this.stats = stats;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        for (int i = 0; i < stats.length; i++) {
            stats[i] = buf.readByte();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        for (byte stat : stats) {
            buf.writeByte(stat);
        }
    }

    @Override
    public IMessage onMessage(CharacterMessage message, MessageContext ctx) {
        return null;
    }
}
