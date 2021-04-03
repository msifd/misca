package msifeed.misca.combat.cap;

import msifeed.misca.combat.CharAttribute;
import msifeed.misca.combat.Combat;
import msifeed.misca.rolls.Dices;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class CombatantEventHandler {
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityLivingBase)
            event.addCapability(ICombatant.KEY, new CombatantProvider());
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        final ICombatant original = CombatantProvider.get(event.getOriginal());
        final ICombatant cloned = CombatantProvider.get(event.getEntityPlayer());
        cloned.replaceWith(original);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.world.isRemote) {
            Combat.MANAGER.rejoinToBattle((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.player.world.isRemote) {
            Combat.MANAGER.exitBattle(event.player);
            CombatantProvider.get(event.player).reset();
            CombatantSync.sync((EntityPlayerMP) event.player, event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.player.world.isRemote) {
            Combat.MANAGER.exitBattle(event.player);
            CombatantProvider.get(event.player).reset();
            CombatantSync.sync((EntityPlayerMP) event.player, event.player);
        }
    }

    @SubscribeEvent
    public void onTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityLivingBase) {
            CombatantSync.sync((EntityPlayerMP) event.getEntityPlayer(), (EntityLivingBase) event.getTarget());
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        final Entity entity = event.getEntity();
        if (entity.world.isRemote || entity instanceof EntityPlayer) return;
        if (!(entity instanceof EntityLivingBase)) return;

        setCombatantAttributes((EntityLivingBase) entity, 5, 5);
    }

    private static void setCombatantAttributes(EntityLivingBase entity, int min, int d) {
        for (CharAttribute attr : CharAttribute.values()) {
            final IAttributeInstance inst = entity.getEntityAttribute(attr.attribute);
            if (inst.getBaseValue() < min)
                inst.setBaseValue(Dices.roll(min, d));
        }
    }
}
