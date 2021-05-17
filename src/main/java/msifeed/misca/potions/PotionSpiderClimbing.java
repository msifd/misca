package msifeed.misca.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class PotionSpiderClimbing extends Potion {
    protected PotionSpiderClimbing() {
        super(false, 0x770077);
    }

    public static boolean shouldClimb(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityLivingBase entity) {
        if (!entity.isPotionActive(OtherPotions.spiderClimbing)) return false;
        if (entity.motionY < -1) return false;

        for (EnumFacing f : EnumFacing.HORIZONTALS) {
            final BlockPos p = pos.offset(f);
            if (!world.getBlockState(p).getBlock().isPassable(world, p))
                return true;
        }

        return false;
    }
}
