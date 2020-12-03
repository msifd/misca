package msifeed.sys.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class RpcMessage {
    final String method;
    final Object[] args;

    RpcMessage(String method, Object... args) {
        this.method = method;
        this.args = args;
    }

    RpcMessage(RpcHandlerRegistry registry, RpcCodec codec, ByteBuf buf) throws Exception {
        method = ByteBufUtils.readUTF8String(buf);
        args = new Object[buf.readByte()];

        final Class<?>[] params = registry.getMethodParams(method);
        if (params == null)
            throw new Exception("Unknown RPC method: " + method);
        if (args.length != params.length)
            throw new Exception(String.format("Invalid RPC arg number exp %d, got %d", params.length, args.length));

        for (int i = 0; i < args.length; i++)
            args[i] = codec.decode(params[i], buf);
    }

    ByteBuf encode(RpcCodec codec) {
        final ByteBuf buf = Unpooled.buffer();

        ByteBufUtils.writeUTF8String(buf, method);
        buf.writeByte(args.length);
        for (Object obj : args)
            codec.encode(buf, obj);

        return buf;
    }
}
