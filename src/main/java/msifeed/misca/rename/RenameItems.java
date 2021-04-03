package msifeed.misca.rename;

import msifeed.misca.Misca;

public class RenameItems {
    public static void register() {
        Misca.RPC.register(new RenameRpc());
    }
}
