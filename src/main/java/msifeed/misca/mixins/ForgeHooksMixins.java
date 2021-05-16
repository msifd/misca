package msifeed.misca.mixins;

import msifeed.misca.potions.PotionSpiderClimbing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(value = ForgeHooks.class, remap = false)
public class ForgeHooksMixins {
    @Inject(method = "isLivingOnLadder", at = @At("RETURN"), cancellable = true)
    private static void isLivingOnLadder(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (PotionSpiderClimbing.shouldClimb(world, pos, entity))
                cir.setReturnValue(true);
        }
    }
}
