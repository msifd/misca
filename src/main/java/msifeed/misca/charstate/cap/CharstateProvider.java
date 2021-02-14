package msifeed.misca.charstate.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class CharstateProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ICharstate.class)
    public static Capability<ICharstate> CAP = null;

    private final Capability.IStorage<ICharstate> storage = CAP.getStorage();
    private final ICharstate instance = Objects.requireNonNull(CAP.getDefaultInstance());

    @Nonnull
    public static ICharstate get(@Nonnull EntityLivingBase entity) {
        return Objects.requireNonNull(entity.getCapability(CAP, null));
    }

    public static NBTTagCompound encode(ICharstate state) {
        return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, state, null);
    }

    public static ICharstate decode(NBTTagCompound nbt) {
        final ICharstate state = CAP.getDefaultInstance();
        CAP.getStorage().readNBT(CAP, state, null, nbt);
        return state;
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
