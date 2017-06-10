package ru.ariadna.misca.database;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.TomlConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBHandler {
    public static ChannelsLogger channels = new ChannelsLogger();
    static Logger logger = LogManager.getLogger("Misca-DB");
    private TomlConfig<DBConfigFile> config = new TomlConfig<>(DBConfigFile.class, "database.toml");
    private Connection connection;

    static void asyncUpdate(PreparedStatement s) {
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

        if (!connectToDB() || connection == null) {
            return;
        }

        channels.init(connection, config.get().chat_table);
        logger.info("Misca successfully connected to database.");
    }

    private boolean connectToDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            DBConfigFile.DB config = this.config.get().database;
            String url = String.format("jdbc:mysql://%s:%d/%s", config.host, config.port, config.database);
            connection = DriverManager.getConnection(url, config.username, config.password);

            return true;
        } catch (SQLException e) {
            logger.error("Failed to connect to database! {}", e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error("Cannot find database driver!");
        }
        return false;
    }

    private static class DBConfigFile {
        DB database = new DB();
        String chat_table = "chat_logs";

        static class DB {
            String host = "localhost";
            int port = 3306;
            String database = "ariadna";
            String username = "r00t";
            String password = "swordfish";
        }
    }
}
