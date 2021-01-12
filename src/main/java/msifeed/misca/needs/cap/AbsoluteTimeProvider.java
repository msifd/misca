package msifeed.misca.needs.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class AbsoluteTimeProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(IAbsoluteTime.class)
    public static Capability<IAbsoluteTime> CAP = null;

    private final Capability.IStorage<IAbsoluteTime> storage = CAP.getStorage();
    private final IAbsoluteTime instance = Objects.requireNonNull(CAP.getDefaultInstance());

    @Nonnull
    public static IAbsoluteTime get(@Nonnull EntityLivingBase entity) {
        return Objects.requireNonNull(entity.getCapability(CAP, null));
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
