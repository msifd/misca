package msifeed.misca.locks.cap.pick;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class LockPickStorage implements Capability.IStorage<ILockPick> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ILockPick> capability, ILockPick instance, EnumFacing side) {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("secret", instance.getSecret());
        nbt.setLong("pos", instance.getPos().toLong());

        return nbt;
    }

    @Override
    public void readNBT(Capability<ILockPick> capability, ILockPick instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;
        instance.setLock(BlockPos.fromLong(nbt.getInteger("pos")), nbt.getInteger("secret"));
    }
}
