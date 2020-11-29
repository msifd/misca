package msifeed.misca.locks;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class LockableStorage implements Capability.IStorage<ILockable> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ILockable> capability, ILockable instance, EnumFacing side) {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean(Tag.locked, instance.isLocked());
        nbt.setString(Tag.secret, instance.getSecret());

        return nbt;
    }

    @Override
    public void readNBT(Capability<ILockable> capability, ILockable instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;
        instance.setLocked(nbt.getBoolean(Tag.locked));
        instance.setSecret(nbt.getString(Tag.secret));
    }

    private static class Tag {
        private static final String locked = "locked";
        private static final String secret = "secret";
    }
}
