package msifeed.misca.needs;

import msifeed.misca.Misca;
import msifeed.misca.needs.cap.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
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
        MinecraftForge.EVENT_BUS.register(this);
        Misca.RPC.register(new PlayerNeedsSync());
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

        integrityHandler.handleTime(player, needs, absSec);
        sanityHandler.handleTime(player, needs, relSec);
        staminaHandler.handleTime(needs, absSec);
        corruptionHandler.handleTime(needs, absSec);

        if (player instanceof EntityPlayerMP) {
            PlayerNeedsSync.sync((EntityPlayerMP) player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerHurt(LivingHurtEvent event) {
        if (event.isCanceled() || event.getAmount() < 1) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        if (event.getEntity().world.isRemote) return;
        if (event.getSource().canHarmInCreative()) return;

        final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        final IPlayerNeeds needs = PlayerNeedsProvider.get(player);
        integrityHandler.handleDamage(needs, event.getAmount());
        sanityHandler.handleDamage(needs, event.getAmount());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntityPlayer().getHeldItemMainhand().isEmpty()) return;

        final IPlayerNeeds needs = PlayerNeedsProvider.get(event.getEntityPlayer());
        staminaHandler.handleMining(event, needs);
    }
}
