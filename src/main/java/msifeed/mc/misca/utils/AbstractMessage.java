package msifeed.mc.misca.utils;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;

public abstract class AbstractMessage<SELF extends AbstractMessage<SELF>> implements IMessage, IMessageHandler<SELF, IMessage> {

    public String readShortString(ByteBuf buf) {
        final byte len = buf.readByte();
        return new String(buf.readBytes(len).array());
    }

    public void writeShortString(ByteBuf buf, String str) {
        final byte[] bytes = str.getBytes(Charsets.UTF_8);
        buf.writeByte(bytes.length);
        buf.writeBytes(bytes);
    }

    public String readString(ByteBuf buf) {
        final int len = buf.readInt();
        return new String(buf.readBytes(len).array());
    }

    public void writeString(ByteBuf buf, String str) {
        final byte[] bytes = str.getBytes(Charsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }
}
