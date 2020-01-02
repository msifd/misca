package msifeed.mc.aorta.locks.items;

import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.aorta.locks.LockObject;
import msifeed.mc.aorta.locks.LockType;
import msifeed.mc.aorta.locks.Locks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class KeyItem extends Item {
    public static final String ID = "lock_key";
    public static final String TEX_BASE = "lock_key_";
    private static final int ICONS_COUNT = 3;

    private IIcon[] icons = new IIcon[ICONS_COUNT];

    public KeyItem() {
        setUnlocalizedName(ID);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!itemStack.hasTagCompound())
            return false;

        final LockObject lock = LockObject.find(world, x, y, z);
        if (lock == null || lock.getLockType() == LockType.DIGITAL)
            return false;

        final String secret = itemStack.getTagCompound().getString("secret");
        if (lock.canUnlockWith(secret)) {
            lock.toggleLocked();

            if (!world.isRemote) {
                final String msg = lock.isLocked() ? "aorta.lock.locked" : "aorta.lock.unlocked";
                player.addChatMessage(new ChatComponentTranslation(msg));
            }
        }

        return true;
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        if (!stack.hasTagCompound() || stack.getTagCompound().getString("secret").isEmpty())
            return icons[0];

        final String secret = stack.getTagCompound().getString("secret");
        final int iconId = secret.charAt(1) % ICONS_COUNT; // use second char, because first one can be minus
        return icons[iconId];
    }

    @Override
    public void registerIcons(IIconRegister p_94581_1_) {
        for (int i = 0; i < ICONS_COUNT; ++i)
            icons[i] = p_94581_1_.registerIcon(Locks.MODID + ":" + TEX_BASE + (i + 1));
    }

    public static ItemStack makeKeyItem(String secret) {
        final ItemStack stack = GameRegistry.findItemStack(Locks.MODID, KeyItem.ID, 1);

        final NBTTagCompound compound = new NBTTagCompound();
        compound.setString("secret", secret);
        stack.setTagCompound(compound);

        return stack;
    }
}
