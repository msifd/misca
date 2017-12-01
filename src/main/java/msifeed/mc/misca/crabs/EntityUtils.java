package msifeed.mc.misca.crabs;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import java.util.UUID;

public class EntityUtils {
    public static EntityPlayer lookupPlayer(UUID uuid) {
        if (FMLCommonHandler.instance().getSide().isClient())
            return Minecraft.getMinecraft().theWorld.func_152378_a(uuid);

        for (WorldServer world : MinecraftServer.getServer().worldServers) {
            final EntityPlayer p = world.func_152378_a(uuid);
            if (p != null) return p;
        }

        return null;
    }
}
