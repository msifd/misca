package msifeed.sys.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public abstract class CapElbSync {
    protected abstract void sync(EntityPlayerMP receiver, EntityLivingBase target);

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.world.isRemote)
            sync((EntityPlayerMP) event.player, event.player);
    }

    @SubscribeEvent
    public void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.player.world.isRemote)
            sync((EntityPlayerMP) event.player, event.player);
    }

    @SubscribeEvent
    public void onPlayerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.player.world.isRemote)
            sync((EntityPlayerMP) event.player, event.player);
    }

    @SubscribeEvent
    public void onTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityLivingBase)
            sync((EntityPlayerMP) event.getEntityPlayer(), (EntityLivingBase) event.getTarget());
    }
}
