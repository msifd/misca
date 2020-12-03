package msifeed.sys.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.lang.ref.WeakReference;
import java.util.List;

@ChannelHandler.Sharable
public class RpcPacketCodec extends MessageToMessageCodec<FMLProxyPacket, RpcMessage> {
    private final RpcHandlerRegistry registry;
    private final RpcCodec codec;

    public RpcPacketCodec(RpcHandlerRegistry registry, RpcCodec codec) {
        this.registry = registry;
        this.codec = codec;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
        super.handlerAdded(ctx);
        ctx.channel().attr(FMLIndexedMessageToMessageCodec.INBOUNDPACKETTRACKER).set(new ThreadLocal<>());
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, List<Object> out) throws Exception {
        final String channel = ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get();
        final FMLProxyPacket proxy = new FMLProxyPacket(new PacketBuffer(msg.encode(codec)), channel);

        final WeakReference<FMLProxyPacket> ref = ctx.channel().attr(FMLIndexedMessageToMessageCodec.INBOUNDPACKETTRACKER).get().get();
        final FMLProxyPacket old = ref == null ? null : ref.get();
        if (old != null)
            proxy.setDispatcher(old.getDispatcher());
        out.add(proxy);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
        final ByteBuf payload = msg.payload().duplicate();
        if (payload.readableBytes() < 1) {
            FMLLog.log.error("The RpcPacketCodec has received an empty buffer on channel {}, likely a result of a LAN server issue. Pipeline parts : {}", ctx.channel().attr(NetworkRegistry.FML_CHANNEL), ctx.pipeline().toString());
            return;
        }

        final RpcMessage message = new RpcMessage(registry, codec, payload);
        ctx.channel().attr(FMLIndexedMessageToMessageCodec.INBOUNDPACKETTRACKER).get().set(new WeakReference<>(msg));
        out.add(message);
        payload.release();
    }
}
