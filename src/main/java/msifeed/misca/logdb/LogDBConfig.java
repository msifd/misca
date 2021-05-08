package msifeed.misca.logdb;

import msifeed.misca.Misca;
import net.minecraftforge.common.config.Config;

@Config(modid = Misca.MODID, category = "log_db")
public class LogDBConfig {
    public static boolean disabled = true;

    public static String host = "localhost";
    public static int port = 3306;
    public static String database = "ariadna";
    public static String username = "marduk";
    public static String password = "swordfish";

    public static String table = "chat_logs";
    public static int timezone = 3;
}
