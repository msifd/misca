package msifeed.misca.locks.items;

import msifeed.misca.Misca;
import msifeed.misca.locks.LockType;
import msifeed.misca.locks.LockUtils;
import msifeed.misca.locks.Locks;
import msifeed.misca.locks.LocksConfig;
import msifeed.misca.locks.cap.ILockHolder;
import msifeed.misca.locks.cap.LockAccessor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLock extends Item {
    public static final String ID_BASE = "lock_";

    public ItemLock(LockType type) {
        setRegistryName(Misca.MODID, ID_BASE + type.name());
        setTranslationKey(ID_BASE + type.name());
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) return EnumActionResult.PASS;

        if (player.world.isRemote) {
            if (LockAccessor.isLocked(worldIn, pos)) return EnumActionResult.FAIL;
            else return EnumActionResult.SUCCESS;
        }

        final LocksConfig config = Misca.getSharedConfig().locks;

        final int pins = getNumberOfPins(player.getHeldItem(hand));
        final int positions = getNumberOfPinPositions(player.getHeldItem(hand));
        final int secret = LockUtils.generateSecret(pins, positions);

        final ILockHolder currentLock = LockAccessor.createWrap(worldIn, pos);

        if (!Locks.addLock(worldIn, pos, secret)) {
            sendStatus(player, "Failed to set lock", TextFormatting.RED);
            return EnumActionResult.FAIL;
        }

        final ItemStack lockStack = player.getHeldItem(hand);
        lockStack.shrink(1);

        final ItemStack keys = ItemKey.createKey(secret);
        keys.setCount(config.setupKeysCount);
        player.addItemStackToInventory(keys);

        sendStatus(player, "Lock set " + LockUtils.toHex(secret), TextFormatting.GREEN);
        return EnumActionResult.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("Pins: " + getNumberOfPins(stack));
        tooltip.add("Pin positions: " + getNumberOfPinPositions(stack));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab))         {
            items.add(new ItemStack(this, 1, 42));
            items.add(new ItemStack(this, 1, 96));
            items.add(new ItemStack(this, 1, 158));
        }
    }

    private static int getNumberOfPins(ItemStack stack) {
        final int meta = stack.getItemDamage() % 10;
        return MathHelper.clamp(meta, 1, 8);
    }

    private static int getNumberOfPinPositions(ItemStack stack) {
        final int meta = stack.getItemDamage() / 10;
        if (meta <= 0)
            return Misca.getSharedConfig().locks.defaultPinPositions;
        return MathHelper.clamp(meta, 1, 15);
    }

    private static void sendStatus(EntityPlayer player, String message, TextFormatting color) {
        final ITextComponent te = new TextComponentString(message);
        te.getStyle().setColor(color);
        player.sendStatusMessage(te, true);
    }
}
