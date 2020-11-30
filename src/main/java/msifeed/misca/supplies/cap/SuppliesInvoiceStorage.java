package msifeed.misca.supplies.cap;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SuppliesInvoiceStorage implements Capability.IStorage<ISuppliesInvoice> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ISuppliesInvoice> capability, ISuppliesInvoice instance, EnumFacing side) {
        final NBTTagCompound nbt = new NBTTagCompound();
        if (instance.isEmpty()) return nbt;

        nbt.setLong("Last", instance.getLastDeliveryTime());
        nbt.setLong("Int", instance.getDeliveryInterval());
        nbt.setInteger("MaxSeq", instance.getMaxDeliverySequence());
        ItemStackHelper.saveAllItems(nbt, instance.getProducts());

        return nbt;
    }

    @Override
    public void readNBT(Capability<ISuppliesInvoice> capability, ISuppliesInvoice instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;

        final int productsSize = nbt.getTagList("Items", 10).tagCount();
        if (productsSize <= 0) return;

        final NonNullList<ItemStack> products = NonNullList.withSize(productsSize, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, products);
        instance.setProducts(products);

        instance.setLastDeliveryTime(nbt.getLong("Last"));
        instance.setDeliveryInterval(nbt.getLong("Int"));
        instance.setMaxDeliverySequence(nbt.getInteger("MaxSeq"));
    }
}
