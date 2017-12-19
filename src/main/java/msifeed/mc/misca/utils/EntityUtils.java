package msifeed.mc.misca.utils;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.stream.Stream;

public class EntityUtils {
    public static UUID getUuid(EntityLivingBase e) {
        if (e instanceof EntityPlayer) return e.getUniqueID();
        else {
            final byte[] worldSalt = Ints.toByteArray(e.worldObj.provider.dimensionId);
            final byte[] entityId = Ints.toByteArray(e.getEntityId());
            return UUID.nameUUIDFromBytes(Bytes.concat(worldSalt, entityId));
        }
    }

    public static EntityLivingBase lookupPlayer(UUID uuid) {
        final World[] worlds;
        if (FMLCommonHandler.instance().getSide().isServer()) worlds = MinecraftServer.getServer().worldServers;
        else worlds = new World[]{Minecraft.getMinecraft().theWorld};

        for (World w : worlds) {
            for (Object o : w.loadedEntityList) {
                if (o instanceof EntityLivingBase) {
                    final EntityLivingBase e = (EntityLivingBase) o;
                    if (getUuid(e).equals(uuid)) return e;
                }
            }
        }

        return null;
    }

    public static Stream<EntityPlayerMP> getPlayersAround(EntityLivingBase center, int distance) {
        return ((Stream<EntityPlayerMP>) center.worldObj.playerEntities.stream())
                .filter(player -> player.getDistanceToEntity(center) <= distance);
    }
}
