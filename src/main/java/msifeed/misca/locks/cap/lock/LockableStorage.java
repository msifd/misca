package msifeed.misca.locks.cap.lock;

import msifeed.misca.locks.LockType;
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
        nbt.setBoolean("locked", instance.isLocked());
        nbt.setInteger("secret", instance.getSecret());
        nbt.setByte("type", (byte) instance.getType().ordinal());
        return nbt;
    }

    @Override
    public void readNBT(Capability<ILockable> capability, ILockable instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;
        instance.setLocked(nbt.getBoolean("locked"));
        instance.setSecret(nbt.getInteger("secret"));
        instance.setType(LockType.values()[nbt.getByte("type")]);
    }
}
