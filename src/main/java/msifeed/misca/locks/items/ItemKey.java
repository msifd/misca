package msifeed.misca.locks.items;

import msifeed.misca.locks.LockItems;
import msifeed.misca.locks.LockUtils;
import msifeed.misca.locks.Locks;
import msifeed.misca.locks.cap.key.ILockKey;
import msifeed.misca.locks.cap.key.LockKeyProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class ItemKey extends Item implements IUnlockTool {
    public static final String ID = "mechanical_key";

    public static ItemStack createKey(int secret) {
        final ItemStack stack = new ItemStack(LockItems.key, 1, 1);
        final ILockKey key = LockKeyProvider.get(stack);
        key.setSecret(secret);
        return stack;
    }

    public ItemKey() {
        setTranslationKey(ID);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (stack.getMetadata() == 0)
            return "item.blank_key";
        else
            return super.getTranslationKey(stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) return EnumActionResult.PASS;
        if (player.getHeldItem(hand).getMetadata() == 0) return EnumActionResult.FAIL;

        final ILockKey key = LockKeyProvider.get(player.getHeldItem(hand));
        if (key == null) {
            sendStatus(player, "Bad key", TextFormatting.RED);
            return EnumActionResult.FAIL;
        }

        if (Locks.toggleLock(world, pos, key.getSecret())) {
            return EnumActionResult.SUCCESS;
        } else {
            sendStatus(player, "Can't toggle the lock", TextFormatting.RED);
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        final ILockKey key = LockKeyProvider.get(stack);
        tooltip.add("Secret: " + LockUtils.toHex(key.getSecret()));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        final LockKeyProvider provider = new LockKeyProvider();
        if (nbt != null) provider.deserializeNBT(nbt.getTag("Key"));
        return provider;
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        final ILockKey key = LockKeyProvider.get(stack);
        final NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        nbt.setTag("Key", LockKeyProvider.CAP.writeNBT(key, null));
        return nbt;
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
        stack.setTagCompound(nbt);
        if (nbt != null) {
            final ILockKey key = LockKeyProvider.get(stack);
            LockKeyProvider.CAP.readNBT(key, null, nbt.getTag("Key"));
        }
    }

    public static boolean isBlank(ItemStack stack) {
        return stack.getItemDamage() == 0;
    }

    private static void sendStatus(EntityPlayer player, String message, TextFormatting color) {
        final ITextComponent te = new TextComponentString(message);
        te.getStyle().setColor(color);
        player.sendStatusMessage(te, true);
    }
}
