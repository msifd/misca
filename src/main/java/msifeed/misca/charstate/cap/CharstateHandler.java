package msifeed.misca.charstate.cap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CharstateHandler {
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(ICharstate.KEY, new CharstateProvider());
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        final ICharstate original = CharstateProvider.get(event.getOriginal());
        final ICharstate cloned = CharstateProvider.get(event.getEntityPlayer());
        cloned.replaceWith(original);
        CharstateSync.sync(event.getEntityPlayer());
    }
}
