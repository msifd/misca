package msifeed.misca.tweaks;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

public class DisableSomeWorkstations {
    private static final Set<ResourceLocation> DENIED = Sets.newHashSet(
            Blocks.ENCHANTING_TABLE.getRegistryName(),
            Blocks.BREWING_STAND.getRegistryName()
    );

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        final Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (DENIED.contains(block.getRegistryName())) {
            event.setUseBlock(Event.Result.DENY);
        }
    }
}
