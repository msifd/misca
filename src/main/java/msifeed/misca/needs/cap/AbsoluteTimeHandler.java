package msifeed.misca.needs.cap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AbsoluteTimeHandler {
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(IAbsoluteTime.KEY, new AbsoluteTimeProvider());
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        final IAbsoluteTime original = AbsoluteTimeProvider.get(event.getOriginal());
        final IAbsoluteTime cloned = AbsoluteTimeProvider.get(event.getEntityPlayer());
        cloned.replaceWith(original);
    }
}
