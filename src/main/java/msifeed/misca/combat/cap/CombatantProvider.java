package msifeed.misca.combat.cap;

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

public class CombatantProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ICombatant.class)
    public static Capability<ICombatant> CAP = null;

    private final Capability.IStorage<ICombatant> storage = CAP.getStorage();
    private final ICombatant instance = Objects.requireNonNull(CAP.getDefaultInstance());

    @Nonnull
    public static ICombatant get(@Nonnull EntityLivingBase entity) {
        return Objects.requireNonNull(entity.getCapability(CAP, null));
    }

    public static NBTTagCompound encode(ICombatant charsheet) {
        return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, charsheet, null);
    }

    public static ICombatant decode(NBTTagCompound nbt) {
        final ICombatant cs = CAP.getDefaultInstance();
        CAP.getStorage().readNBT(CAP, cs, null, nbt);
        return cs;
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
