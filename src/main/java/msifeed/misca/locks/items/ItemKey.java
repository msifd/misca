package msifeed.misca.locks.items;

import msifeed.misca.Misca;
import msifeed.misca.locks.LockItems;
import msifeed.misca.locks.Locks;
import msifeed.misca.locks.cap.key.ILockKey;
import msifeed.misca.locks.cap.key.LockKeyProvider;
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

public class ItemKey extends Item {
    public static final String ID = "mechanical_key";

    public static ItemStack createKey(int secret) {
        final ItemStack stack = new ItemStack(LockItems.key);
        final ILockKey key = LockKeyProvider.get(stack);
        if (key != null) key.setSecret(secret);
        return stack;
    }

    public ItemKey() {
        setRegistryName(Misca.MODID, ID);
        setTranslationKey(ID);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new LockKeyProvider();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) return EnumActionResult.PASS;

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

    private static void sendStatus(EntityPlayer player, String message, TextFormatting color) {
        final ITextComponent te = new TextComponentString(message);
        te.getStyle().setColor(color);
        player.sendStatusMessage(te, true);
    }
}
