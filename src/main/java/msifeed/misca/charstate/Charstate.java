package msifeed.misca.charstate;

import msifeed.misca.Misca;
import msifeed.misca.charstate.cap.*;
import msifeed.misca.charstate.handler.*;
import msifeed.misca.chatex.SpeechEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Charstate {
    private static final int UPDATE_PER_TICKS = 40;
    private static final int SYNC_PER_TICKS = 80;

    private final IntegrityHandler integrityHandler = new IntegrityHandler();
    private final SanityHandler sanityHandler = new SanityHandler();
    private final StaminaHandler staminaHandler = new StaminaHandler();
    private final CorruptionHandler corruptionHandler = new CorruptionHandler();
    private final EffortsHandler effortsHandler = new EffortsHandler();

    public void preInit() {
        CapabilityManager.INSTANCE.register(ICharstate.class, new CharstateStorage(), CharstateImpl::new);
        MinecraftForge.EVENT_BUS.register(new CharstateHandler());
        MinecraftForge.EVENT_BUS.register(this);
        Misca.RPC.register(new CharstateSync());
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

        final ICharstate state = CharstateProvider.get(player);
        final long absSec = state.consumeTime();
        final long relSec = UPDATE_PER_TICKS / 20;

        integrityHandler.handleTime(player, absSec);
        sanityHandler.handleTime(player, relSec);
        staminaHandler.handleTime(player, absSec);
        corruptionHandler.handleTime(player, absSec);
        effortsHandler.handleTime(player, absSec);

        if (event.getEntity().ticksExisted % SYNC_PER_TICKS == 1)
            CharstateSync.sync(player);
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
    public void onItemUse(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

        sanityHandler.handleItemUse((EntityPlayer) event.getEntityLiving(), event.getResultStack());
    }

    @SubscribeEvent
    public void onSpeech(SpeechEvent event) {
        sanityHandler.handleSpeech(event.getPlayer(), event.range, event.getMessage());
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
