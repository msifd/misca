package msifeed.misca.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFrostedIce;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockFrostedIce.class)
public abstract class BlockFrostedIceMixin {

    @Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;scheduleUpdate(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V"))
    public void updateTick(World world, BlockPos pos, Block blockIn, int delay) {
        // Do not schedule for update
    }
}
