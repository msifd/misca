package ru.ariadna.misca.tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Отменяет взаимодействие с варочной стойкой и столом зачарования
 */
public class DisableSomeCraftingTables {
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
            return;

        Block block = event.world.getBlock(event.x, event.y, event.z);
        if (block instanceof BlockBrewingStand || block instanceof BlockEnchantmentTable) {
            event.setCanceled(true);
//            event.entityPlayer.displayGUIWorkbench(event.x, event.y, event.z);
        }
    }
}
