package msifeed.misca.needs.cap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerNeedsHandler {
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(IPlayerNeeds.KEY, new PlayerNeedsProvider());
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        final IPlayerNeeds original = PlayerNeedsProvider.get(event.getOriginal());
        final IPlayerNeeds cloned = PlayerNeedsProvider.get(event.getEntityPlayer());
        cloned.replaceWith(original);
    }
}
