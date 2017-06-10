package ru.ariadna.misca.database;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChannelsLogger {
    private static Connection connection;
    private static String chat_table;

    public static void logMessage(ICommandSender sender, String cmd, String text) {
        if (connection == null) return;

        try {
            String query = "INSERT INTO `" + chat_table +
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

            DBHandler.asyncUpdate(s);

        } catch (SQLException e) {
            DBHandler.logger.error("Failed to prepare chat log sql! {}", e);
        }
    }

    void init(Connection conn, String table) {
        connection = conn;
        chat_table = table;
    }
}
