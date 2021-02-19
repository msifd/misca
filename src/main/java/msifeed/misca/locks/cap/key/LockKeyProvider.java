package msifeed.misca.locks.cap.key;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class LockKeyProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ILockKey.class)
    public static Capability<ILockKey> CAP = null;

    private final Capability.IStorage<ILockKey> storage = CAP.getStorage();
    private final ILockKey instance = Objects.requireNonNull(CAP.getDefaultInstance());

    @Nullable
    public static ILockKey get(ItemStack stack) {
        return stack.getCapability(LockKeyProvider.CAP, null);
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
