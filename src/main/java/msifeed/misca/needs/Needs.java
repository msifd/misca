package msifeed.misca.needs;

import msifeed.misca.needs.cap.*;
import msifeed.misca.needs.handler.CorruptionHandler;
import msifeed.misca.needs.handler.IntegrityHandler;
import msifeed.misca.needs.handler.SanityHandler;
import msifeed.misca.needs.handler.StaminaHandler;
import msifeed.misca.needs.potion.NeedsPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Needs {
    private static final int UPDATE_PER_TICKS = 40;

    private final IntegrityHandler integrityHandler = new IntegrityHandler();
    private final SanityHandler sanityHandler = new SanityHandler();
    private final StaminaHandler staminaHandler = new StaminaHandler();
    private final CorruptionHandler corruptionHandler = new CorruptionHandler();

    public void preInit() {
        CapabilityManager.INSTANCE.register(IPlayerNeeds.class, new PlayerNeedsStorage(), PlayerNeeds::new);
        MinecraftForge.EVENT_BUS.register(new PlayerNeedsHandler());
        MinecraftForge.EVENT_BUS.register(new NeedsPotions());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerCreate(EntityEvent.EntityConstructing event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        final AbstractAttributeMap attributes = ((EntityLivingBase) event.getEntity()).getAttributeMap();
        attributes.registerAttribute(IntegrityHandler.INTEGRITY);
        attributes.registerAttribute(SanityHandler.SANITY);
        attributes.registerAttribute(StaminaHandler.STAMINA);
        attributes.registerAttribute(CorruptionHandler.CORRUPTION);
    }

    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        if (event.getEntity().ticksExisted % UPDATE_PER_TICKS != 1) return;
        if (event.getEntity().world.isRemote) return;

        final EntityPlayer player = (EntityPlayer) event.getEntityLiving();

        final IPlayerNeeds needs = PlayerNeedsProvider.get(player);
        final long absSec = needs.consumeTime();
        final long relSec = UPDATE_PER_TICKS / 20;

        integrityHandler.handleTime(player, absSec);
        sanityHandler.handleTime(player, relSec);
        staminaHandler.handleTime(player, absSec);
        corruptionHandler.handleTime(player, absSec);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerHurt(LivingHurtEvent event) {
        if (event.isCanceled() || event.getAmount() < 1) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        if (event.getEntity().world.isRemote) return;
        if (event.getSource().canHarmInCreative()) return;

        final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        integrityHandler.handleDamage(player, event.getAmount());
        sanityHandler.handleDamage(player, event.getAmount());
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntityPlayer().getHeldItemMainhand().isEmpty()) return;

        staminaHandler.handleMining(event);
    }

    @SubscribeEvent
    public void onItemCrafted(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
        staminaHandler.handleCrafting(event);
    }
}
