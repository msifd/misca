package msifeed.misca.locks.cap.chunk;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class ChunkLockableProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(IChunkLockable.class)
    public static Capability<IChunkLockable> CAP = null;

    private final Capability.IStorage<IChunkLockable> storage = CAP.getStorage();
    private final IChunkLockable instance = Objects.requireNonNull(CAP.getDefaultInstance());

    public static IChunkLockable get(Chunk chunk) {
        return Objects.requireNonNull(chunk.getCapability(ChunkLockableProvider.CAP, null));
    }

    public static NBTTagCompound encode(IChunkLockable cap) {
        return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, cap, null);
    }

    public static IChunkLockable decode(NBTTagCompound nbt) {
        final IChunkLockable cap = CAP.getDefaultInstance();
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
