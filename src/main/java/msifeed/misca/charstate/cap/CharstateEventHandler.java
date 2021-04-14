package msifeed.misca.charstate.cap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CharstateEventHandler {
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(ICharstate.KEY, new CharstateProvider());
    }

    @SubscribeEvent
    public void onPlayerSpawn(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;
        if (event.getEntity() instanceof EntityPlayerMP)
            CharstateSync.sync((EntityPlayerMP) event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityPlayer) {
            CharstateSync.sync((EntityPlayerMP) event.getEntityPlayer(), (EntityPlayer) event.getTarget());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        final ICharstate original = CharstateProvider.get(event.getOriginal());
        final ICharstate cloned = CharstateProvider.get(event.getEntityPlayer());
        cloned.replaceWith(original);
        CharstateSync.sync(event.getEntityPlayer());
    }
}
