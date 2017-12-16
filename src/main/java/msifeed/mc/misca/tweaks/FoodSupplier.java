package msifeed.mc.misca.tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

public class FoodSupplier {
    private static final int TARGET_FOOD_LEVEL = 19;

    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        final FoodStats stats = ((EntityPlayer) event.entityLiving).getFoodStats();
        final int currLevel = stats.getFoodLevel();
        if (currLevel != TARGET_FOOD_LEVEL)
            stats.addStats(TARGET_FOOD_LEVEL - currLevel, 0);
    }

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Pre event) {
        if (event.type == RenderGameOverlayEvent.ElementType.FOOD) event.setCanceled(true);
    }
}
