package msifeed.misca.needs;

import msifeed.misca.Misca;
import msifeed.misca.needs.cap.*;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerNeeds {
    public static final IAttribute SANITY = new RangedAttribute(null, Misca.MODID + ".sanity", 100, 0, 150).setShouldWatch(true);
    public static final IAttribute STAMINA = new RangedAttribute(null, Misca.MODID + ".stamina", 1, 0, 1).setShouldWatch(true);
    public static final IAttribute CORRUPTION = new RangedAttribute(null, Misca.MODID + ".corruption", 0, 0, 100).setShouldWatch(true);

    private static final int UPDATE_PER_TICKS = 40;
    private final IntegrityHandler integrityHandler = new IntegrityHandler();

    public void preInit() {
        CapabilityManager.INSTANCE.register(IAbsoluteTime.class, new AbsoluteTimeStorage(), AbsoluteTime::new);
        MinecraftForge.EVENT_BUS.register(new AbsoluteTimeHandler());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(integrityHandler);
    }

    @SubscribeEvent
    public void onPlayerCreation(EntityEvent.EntityConstructing event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        final EntityPlayer player = (EntityPlayer) event.getEntity();
        final AbstractAttributeMap attributes = player.getAttributeMap();

        attributes.registerAttribute(IntegrityHandler.INTEGRITY);
        attributes.registerAttribute(SANITY);
        attributes.registerAttribute(STAMINA);
        attributes.registerAttribute(CORRUPTION);
    }

    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        if (event.getEntity().ticksExisted % UPDATE_PER_TICKS != 1) return;
        if (event.getEntity().world.isRemote) return;

        final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        final long secs = AbsoluteTimeProvider.get(player).consumeTime();

        final AbstractAttributeMap attributes = player.getAttributeMap();
        integrityHandler.handle(player, attributes.getAttributeInstance(IntegrityHandler.INTEGRITY), secs);
    }
}
