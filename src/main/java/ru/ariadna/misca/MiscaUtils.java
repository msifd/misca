package ru.ariadna.misca;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import ru.ariadna.misca.combat.Combat;

import java.io.IOException;
import java.io.StringReader;

public class MiscaUtils {
    public static boolean isSuperuser(ICommandSender sender) {
        if (sender instanceof MinecraftServer) {
            return true;
        } else if (sender instanceof EntityPlayer) {
            ServerConfigurationManager scm = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
            EntityPlayer pl = (EntityPlayer) sender;
            if (scm.func_152596_g(pl.getGameProfile())) {
                return true;
            }
        }
        return false;
    }

    public static void sendMultiline(ICommandSender sender, String text) {
        String msg = StringEscapeUtils.unescapeJava(text);
        try {
            for (String line : IOUtils.readLines(new StringReader(msg))) {
                sender.addChatMessage(new ChatComponentText(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
