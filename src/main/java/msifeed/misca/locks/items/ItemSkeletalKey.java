package msifeed.misca.locks.items;

import msifeed.misca.Misca;
import msifeed.misca.locks.LockUtils;
import msifeed.misca.locks.Locks;
import msifeed.misca.locks.cap.LockAccessor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemSkeletalKey extends Item implements IUnlockTool {
    public static final String ID = "skeletal_key";

    public ItemSkeletalKey() {
        setRegistryName(Misca.MODID, ID);
        setUnlocalizedName(ID);
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) return EnumActionResult.PASS;

        if (Locks.forceToggleLock(world, pos)) {
            final int secret = LockAccessor.createWrap(world, pos).getSecret();
            sendStatus(player, "It's secret: " + LockUtils.toHex(secret), TextFormatting.GREEN);
            return EnumActionResult.SUCCESS;
        } else {
            sendStatus(player, "Can't find the keyhole.", TextFormatting.RED);
            return EnumActionResult.FAIL;
        }
    }

    private static void sendStatus(EntityPlayer player, String message, TextFormatting color) {
        final ITextComponent te = new TextComponentString(message);
        te.getStyle().setColor(color);
        player.sendStatusMessage(te, true);
    }
}
