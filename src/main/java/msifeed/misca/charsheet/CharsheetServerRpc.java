package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CharsheetServerRpc implements ICharsheetRpc {
    @RpcMethodHandler(update)
    public void onCharsheetUpdate(MessageContext ctx) {
//        Misca.RPC.se
    }
}
