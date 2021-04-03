package msifeed.misca.charsheet.cap;

import msifeed.misca.charsheet.CharSkill;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.keeper.KeeperSync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class CharsheetEventHandler {
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityLivingBase) {
            final AbstractAttributeMap attributes = ((EntityLivingBase) event.getObject()).getAttributeMap();
            attributes.registerAttribute(CharSkill.MOD);

            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(ICharsheet.KEY, new CharsheetProvider());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.world.isRemote) {
            CharsheetSync.sync((EntityPlayerMP) event.player, event.player);
            KeeperSync.INSTANCE.sync((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.player.world.isRemote)
            CharsheetSync.sync((EntityPlayerMP) event.player, event.player);
    }

    @SubscribeEvent
    public void onPlayerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.player.world.isRemote)
            CharsheetSync.sync((EntityPlayerMP) event.player, event.player);
    }

    @SubscribeEvent
    public void onPlayerTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityPlayer) {
            CharsheetSync.sync((EntityPlayerMP) event.getEntityPlayer(), (EntityPlayer) event.getTarget());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        final ICharsheet original = CharsheetProvider.get(event.getOriginal());
        final ICharsheet cloned = CharsheetProvider.get(event.getEntityPlayer());
        cloned.replaceWith(original);
    }
}
