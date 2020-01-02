package msifeed.mc.aorta.sys.rpc;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.io.*;

public class RpcMessage implements IMessage, IMessageHandler<RpcMessage, IMessage> {
    String method;
    Object[] args;

    public RpcMessage() {

    }

    public RpcMessage(String method, Serializable... args) {
        this.method = method;
        this.args = args;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, method);
        buf.writeByte(args.length);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(bos);
            for (Object o : args)
                oos.writeObject(o);
            final byte[] bytes = bos.toByteArray();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        method = ByteBufUtils.readUTF8String(buf);
        args = new Object[buf.readByte()];

        final byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes, 0, bytes.length);
        final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            final ObjectInputStream ois = new ObjectInputStream(bis);
            for (int i = 0; i < args.length; ++i)
                args[i] = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IMessage onMessage(RpcMessage message, MessageContext ctx) {
        Rpc.onMessage(message, ctx);
        return null;
    }
}
