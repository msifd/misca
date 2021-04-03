package msifeed.misca.logdb;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.mariadb.jdbc.MariaDbPoolDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LogDBConnector {
    private static final String QUERY = "INSERT INTO `" + LogDBConfig.table + "` " +
            "(`name`,`uuid`,`time`,`world`,`x`,`y`,`z`,`type`,`text`) " +
            "VALUES (?,?,?,?,?,?,?,?,?);";

    private final MariaDbPoolDataSource pool;

    public LogDBConnector() throws Exception {
        pool = new MariaDbPoolDataSource(LogDBConfig.host, LogDBConfig.port, LogDBConfig.database);
        pool.setUser(LogDBConfig.username);
        pool.setPassword(LogDBConfig.password);
        pool.setLoginTimeout(5);

        pool.getConnection();
    }

    public void log(ICommandSender sender, String type, String text) {
        try (Connection conn = pool.getConnection()) {
            if (conn == null) {
                LogDB.LOG.error("Can't get connection to DB!");
                return;
            }

            final String uuid = (sender instanceof EntityPlayer)
                    ? ((EntityPlayer) sender).getUniqueID().toString()
                    : "";
            final long secs = LocalDateTime.now(ZoneOffset.ofHours(LogDBConfig.timezone)).toEpochSecond(ZoneOffset.UTC);
            final BlockPos pos = new BlockPos(sender.getPositionVector());

            final PreparedStatement s = conn.prepareStatement(QUERY);
            s.setString(1, sender.getName());
            s.setString(2, uuid);
            s.setLong(3, secs);
            s.setString(4, sender.getEntityWorld().getWorldInfo().getWorldName());
            s.setInt(5, pos.getX());
            s.setInt(6, pos.getY());
            s.setInt(7, pos.getZ());
            s.setString(8, type);
            s.setString(9, text);
            s.executeUpdate();
        } catch (SQLException e) {
            LogDB.LOG.error("Failed to send log to the database", e);
        }
    }
}
