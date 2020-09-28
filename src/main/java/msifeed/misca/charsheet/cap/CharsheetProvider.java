package msifeed.misca.charsheet.cap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class CharsheetProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ICharsheet.class)
    public static Capability<ICharsheet> CAP = null;

    private final Capability.IStorage<ICharsheet> storage = CAP.getStorage();
    private final ICharsheet instance = CAP.getDefaultInstance();

    @Nonnull
    public static ICharsheet get(EntityPlayer player) {
        return Objects.requireNonNull(player.getCapability(CharsheetProvider.CAP, null));
    }

    public static NBTTagCompound encode(ICharsheet charsheet) {
        return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, charsheet, null);
    }

    public static ICharsheet decode(NBTTagCompound nbt) {
        final ICharsheet cs = CAP.getDefaultInstance();
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
