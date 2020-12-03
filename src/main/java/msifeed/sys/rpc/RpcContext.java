package msifeed.sys.rpc;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.relauncher.Side;

public class RpcContext {
    public final INetHandler netHandler;
    public final Side side;

    RpcContext(INetHandler netHandler, Side side) {
        this.netHandler = netHandler;
        this.side = side;
    }

    public NetHandlerPlayServer getServerHandler()
    {
        return (NetHandlerPlayServer) netHandler;
    }
    public NetHandlerPlayClient getClientHandler()
    {
        return (NetHandlerPlayClient) netHandler;
    }
}
