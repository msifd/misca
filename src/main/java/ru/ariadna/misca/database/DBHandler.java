package ru.ariadna.misca.database;

import com.google.common.eventbus.Subscribe;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.Misca;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBHandler {
    public static ChannelsLogger channels = new ChannelsLogger();
    static Logger logger = LogManager.getLogger("Misca-DB");
    private File configFile;
    private DBConfigFile config = new DBConfigFile();
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

        configFile = new File(Misca.config_dir, "database.toml");

        if (!configFile.exists()) {
            createConfigFile();
        }
        if (!configFile.exists() || !readConfig() || !connectToDB() || connection == null) {
            return;
        }

        channels.init(connection, config.chat_table);
        logger.info("Misca successfully connected to database.");
    }

    private void createConfigFile() {
        TomlWriter tomlWriter = new TomlWriter();
        try {
            tomlWriter.write(config, configFile);
        } catch (IOException e) {
            logger.error("Failed to create database config! {}", e);
        }
    }

    private boolean readConfig() {
        Toml toml = new Toml();
        try {
            byte[] encoded = Files.readAllBytes(configFile.toPath());
            String configContent = new String(encoded);

            if (configContent.trim().isEmpty()) {
                logger.error("Database config is empty! Skipping.");
                return false;
            }

            config = toml.read(configContent).to(DBConfigFile.class);
            return true;
        } catch (IllegalStateException e) {
            logger.error("Mistake in database config! {}", e);
        } catch (IOException e) {
            logger.error("Failed to read database config! {}", e);
        }
        return false;
    }

    private boolean connectToDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            DBConfigFile.DB config = this.config.database;
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

    private class DBConfigFile {
        DB database = new DB();
        String chat_table = "chat_logs";

        class DB {
            String host = "localhost";
            int port = 3306;
            String database = "ariadna";
            String username = "r00t";
            String password = "swordfish";
        }
    }
}
