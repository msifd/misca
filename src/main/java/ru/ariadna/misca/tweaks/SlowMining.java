package ru.ariadna.misca.tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Замедляет копание иструментами. Пустую руку не затрагивает.
 */
public class SlowMining {
    private ConfigSection config;

    public void init() {
        config = Tweaks.config.config().slow_mining;

        if (config.enabled) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack held_item = event.entityPlayer.getHeldItem();

        if (held_item == null || !(held_item.getItem() instanceof ItemTool)) {
            return;
        }

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
