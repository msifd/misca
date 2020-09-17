package msifeed.sys.rpc;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RpcMessage implements IMessage {
    String method;
    Object[] args;

    public RpcMessage() {
        // For incoming messages
    }

    RpcMessage(String method, Object... args) {
        this.method = method;
        this.args = args;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, method);
        buf.writeByte(args.length);
        for (Object obj : args)
            RpcCodec.INSTANCE.encode(buf, obj);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        method = ByteBufUtils.readUTF8String(buf);
        args = new Object[buf.readByte()];
        for (int i = 0; i < args.length; i++)
            args[i] = RpcCodec.INSTANCE.decode(buf);
    }
}
