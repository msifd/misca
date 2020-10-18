package msifeed.misca.rename;

import msifeed.misca.Misca;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class RenameItems {
    public static void register() {
        Misca.RPC.register(new RenameServerRpc());

        if (FMLCommonHandler.instance().getSide().isClient())
            Misca.RPC.register(new RenameClientRpc());
    }
}
