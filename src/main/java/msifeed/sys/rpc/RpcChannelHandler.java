package msifeed.sys.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class RpcChannelHandler extends SimpleChannelInboundHandler<RpcMessage> {
    private final RpcChannel rpc;
    private final Side side;

    public RpcChannelHandler(RpcChannel rpc, Side side) {
        this.rpc = rpc;
        this.side = side;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        final INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(() -> {
            rpc.invoke(new RpcContext(netHandler, side), msg);
        });
    }
}
