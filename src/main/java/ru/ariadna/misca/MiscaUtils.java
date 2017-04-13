package ru.ariadna.misca;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

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
}
