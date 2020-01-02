package msifeed.mc.aorta.locks.items;

import msifeed.mc.aorta.locks.LockObject;
import msifeed.mc.aorta.locks.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class AdvancedLockpickItem extends LockpickItem {
    public static final String ID = "lock_advanced_lockpick";

    public AdvancedLockpickItem() {
        setUnlocalizedName(ID);
        setTextureName(Locks.MODID + ":" + SkeletalKeyItem.ID);
    }

    @Override
    protected boolean rollPick(LockObject lock, ItemStack pick, EntityPlayer player) {
        consumePick(lock, pick, player, 0);
        return true;
    }

    @Override
    protected void consumePick(LockObject lock, ItemStack pick, EntityPlayer player, int roll) {
        if (lock.getTileEntity().getWorldObj().isRemote)
            return;
        pick.stackSize--;
        makeBreakSound(lock);
    }
}
