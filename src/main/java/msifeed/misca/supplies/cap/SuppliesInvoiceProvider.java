package msifeed.misca.supplies.cap;

import net.minecraft.item.ItemStack;
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

public class SuppliesInvoiceProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ISuppliesInvoice.class)
    public static Capability<ISuppliesInvoice> CAP = null;

    private final Capability.IStorage<ISuppliesInvoice> storage = CAP.getStorage();
    private final ISuppliesInvoice instance = Objects.requireNonNull(CAP.getDefaultInstance());

    @Nullable
    public static ISuppliesInvoice get(TileEntity tile) {
        return tile.getCapability(SuppliesInvoiceProvider.CAP, null);
    }

    @Nullable
    public static ISuppliesInvoice get(ItemStack stack) {
        return stack.getCapability(SuppliesInvoiceProvider.CAP, null);
    }

    public static NBTTagCompound encode(ISuppliesInvoice cap) {
        return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, cap, null);
    }

    public static ISuppliesInvoice decode(NBTTagCompound nbt) {
        final ISuppliesInvoice cap = CAP.getDefaultInstance();
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
