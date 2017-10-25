package msifeed.mc.misca.database;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.config.JsonConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum DBHandler {
    INSTANCE;

    private static Logger logger = LogManager.getLogger("Misca-DB");
    private Connection connection;
    private JsonConfig<ConfigSection> config = ConfigManager.getServerConfigFor(ConfigSection.class, "database.json");

    private static void asyncUpdate(PreparedStatement s) {
        Thread thread = new Thread(() -> {
            try {
                s.executeUpdate();
            } catch (SQLException e) {
                logger.error("Failed to update database! {}", e);
            }
        });
        thread.start();
    }

    @Subscribe
    public void onPreInit(FMLPreInitializationEvent event) {
        if (event.getSide().isClient())
            return;

        if (!connectToDB() || connection == null)
            return;

        logger.info("Misca successfully connected to database.");
    }

    public void logMessage(ICommandSender sender, String cmd, String text) {
        if (connection == null) return;

        try {
            String query = "INSERT INTO `" + config.get().chat_table +
                    "` (`chara`,`uuid`,`time`,`world`,`X`,`Y`,`Z`,`command`,`text`) " +
                    "VALUES (?,?,?,?,?,?,?,?,?);";

            String uuid = "";
            if (sender instanceof EntityPlayerMP) {
                uuid = ((EntityPlayerMP) sender).getUniqueID().toString();
            }
            ChunkCoordinates coord = sender.getPlayerCoordinates();

            PreparedStatement s = connection.prepareStatement(query);
            s.setString(1, sender.getCommandSenderName());
            s.setString(2, uuid);
            s.setLong(3, System.currentTimeMillis());
            s.setString(4, sender.getEntityWorld().getWorldInfo().getWorldName());
            s.setInt(5, coord.posX);
            s.setInt(6, coord.posY);
            s.setInt(7, coord.posZ);
            s.setString(8, cmd);
            s.setString(9, text);

            asyncUpdate(s);

        } catch (SQLException e) {
            logger.error("Failed to prepare chat log sql! {}", e);
        }
    }

    private boolean connectToDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            ConfigSection.DB config = this.config.get().database;
            String url = String.format("jdbc:mysql://%s:%d/%s", config.host, config.port, config.database);
            connection = DriverManager.getConnection(url, config.username, config.password);

            return true;
        } catch (SQLException e) {
            logger.error("Failed to connect to database! {}", e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error("Cannot find database driver!");
        } catch (Exception e) {
            logger.error("Database connect exception {}!", e);
        }
        return false;
    }

    public static class ConfigSection {
        DB database = new DB();
        String chat_table = "chat_logs";

        public static class DB {
            String host = "localhost";
            int port = 3306;
            String database = "ariadna";
            String username = "r00t";
            String password = "swordfish";
        }
    }
}
