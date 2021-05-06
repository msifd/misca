package msifeed.misca.charstate.cap;

import msifeed.misca.charstate.handler.CorruptionHandler;
import msifeed.misca.charstate.handler.IntegrityHandler;
import msifeed.misca.charstate.handler.SanityHandler;
import msifeed.misca.charstate.handler.StaminaHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.IAttribute;
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
        if (!(event.getEntity() instanceof EntityPlayerMP)) return;

        final EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
        CharstateSync.sync(player);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        copyAttr(CorruptionHandler.CORRUPTION, event.getOriginal(), event.getEntityPlayer());
        copyAttr(IntegrityHandler.INTEGRITY, event.getOriginal(), event.getEntityPlayer());
        copyAttr(SanityHandler.SANITY, event.getOriginal(), event.getEntityPlayer());
        copyAttr(StaminaHandler.STAMINA, event.getOriginal(), event.getEntityPlayer());

        final ICharstate original = CharstateProvider.get(event.getOriginal());
        final ICharstate cloned = CharstateProvider.get(event.getEntityPlayer());
        cloned.replaceWith(original);

        if (event.getEntityPlayer() instanceof EntityPlayerMP)
            CharstateSync.sync((EntityPlayerMP) event.getEntityPlayer());
    }

    private void copyAttr(IAttribute attr, EntityPlayer src, EntityPlayer dst) {
        dst.getEntityAttribute(attr).setBaseValue(src.getEntityAttribute(attr).getBaseValue());
    }
}
