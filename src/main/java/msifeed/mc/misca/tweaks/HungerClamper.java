package msifeed.mc.misca.tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

public class HungerClamper {
    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        final FoodStats stats = ((EntityPlayer) event.entityLiving).getFoodStats();
        final int currLevel = stats.getFoodLevel();
        if (currLevel > 19) stats.addStats(-1, 0);
        else if (currLevel < 1) stats.addStats(1, 0);
            
//        final int targetLevel = MathHelper.clamp_int(currLevel, 1, 19);
//        if (currLevel != targetLevel)
//            stats.addStats(targetLevel - currLevel, 0);
    }
}