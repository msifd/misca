package msifeed.misca.keeper;

import msifeed.misca.Misca;
import net.minecraftforge.common.config.Config;

@Config(modid = Misca.MODID, category = "keeper_db")
public class KeeperConfig {
    public static boolean disabled = true;

    public static String host = "localhost";
    public static int port = 27017;
    public static String username = "marduk";
    public static String password = "swordfish";
    public static String database = "characters";
    public static String collection = "characters";
}
