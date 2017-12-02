package msifeed.mc.misca.crabs;

import com.google.common.primitives.Ints;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityUtils {
    private static final int UUID_SALT = 1337;

    public static UUID getUuid(Entity e) {
        if (e instanceof EntityPlayer) return e.getUniqueID();
        else return UUID.nameUUIDFromBytes(Ints.toByteArray(UUID_SALT + e.getEntityId()));
    }

    public static EntityLivingBase lookupPlayer(UUID uuid) {
        final World[] worlds;
        if (FMLCommonHandler.instance().getSide().isServer()) worlds = MinecraftServer.getServer().worldServers;
        else worlds = new World[]{Minecraft.getMinecraft().theWorld};

        for (World w : worlds) {
            for (Object o : w.loadedEntityList) {
                if (o instanceof EntityLivingBase) {
                    final EntityLivingBase e = (EntityLivingBase) o;
                    if (e.getPersistentID() == uuid) return e;
                }
            }
        }

        return null;
    }
}
