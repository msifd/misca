package msifeed.misca;

import msifeed.misca.client.ClientConfig;
import msifeed.misca.keeper.KeeperConfig;
import msifeed.misca.logdb.LogDBConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = Misca.MODID)
public class MiscaConfig {
    public static ClientConfig client = new ClientConfig();
    public static ServerConfig server = new ServerConfig();

    public static KeeperConfig keeper = new KeeperConfig();
    public static LogDBConfig logDb = new LogDBConfig();

    public static class ServerConfig {
        public boolean trimCommandSlash = true;
    }
}
