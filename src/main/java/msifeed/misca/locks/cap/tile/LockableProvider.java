package msifeed.misca.locks.cap.tile;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class LockableProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ILockable.class)
    public static Capability<ILockable> CAP = null;

    private final Capability.IStorage<ILockable> storage = CAP.getStorage();
    private final ILockable instance = Objects.requireNonNull(CAP.getDefaultInstance());

    @Nullable
    public static ILockable get(TileEntity tile) {
        return tile.getCapability(LockableProvider.CAP, null);
    }

    public static NBTTagCompound encode(ILockable cap) {
        return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, cap, null);
    }

    public static ILockable decode(NBTTagCompound nbt) {
        final ILockable cap = CAP.getDefaultInstance();
        CAP.getStorage().readNBT(CAP, cap, null, nbt);
        return cap;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CAP ? CAP.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return storage.writeNBT(CAP, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        storage.readNBT(CAP, instance, null, nbt);
    }
}
