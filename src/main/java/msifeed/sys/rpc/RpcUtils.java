package msifeed.sys.rpc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface RpcUtils {
    @Nullable
    static EntityPlayer findPlayer(World world, int entityId) {
        final Entity entity = world.getEntityByID(entityId);
        return entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
    }
    @Nullable
    static EntityPlayerMP findPlayerMP(World world, int entityId) {
        final Entity entity = world.getEntityByID(entityId);
        return entity instanceof EntityPlayerMP ? (EntityPlayerMP) entity : null;
    }
}
