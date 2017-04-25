package ru.ariadna.misca.tweaks;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Замедляет копание иструментами. Пустую руку не затрагивает.
 */
public class SlowMining {
    private static Class<?> tinkerToolClass;
    private ConfigSection config;

    public void init() {
        config = Tweaks.config.config().slow_mining;

        if (config.enabled) {
            MinecraftForge.EVENT_BUS.register(this);

            try {
                tinkerToolClass = Class.forName("tconstruct.library.tools.ToolCore");
                Tweaks.logger.info("Tinker Construct found. Slowing it down...");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack heldItemStack = event.entityPlayer.getHeldItem();
        if (heldItemStack == null) return;

        Item helItem = heldItemStack.getItem();
        boolean isTool = helItem instanceof ItemTool ||
                (tinkerToolClass != null && tinkerToolClass.isInstance(helItem));
        if (!isTool) return;

        if (event.newSpeed == 0) {
            event.newSpeed = event.originalSpeed;
        }
        if (config.coefficient == 0) {
            event.newSpeed = Float.MAX_VALUE;
        } else {
            event.newSpeed /= config.coefficient;
        }
    }

    static class ConfigSection {
        boolean enabled = true;
        float coefficient = 4.0f;
    }
}
