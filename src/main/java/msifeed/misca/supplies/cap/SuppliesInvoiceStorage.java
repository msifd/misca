package msifeed.misca.supplies.cap;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SuppliesInvoiceStorage implements Capability.IStorage<ISuppliesInvoice> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ISuppliesInvoice> capability, ISuppliesInvoice instance, EnumFacing side) {
        final NBTTagCompound nbt = new NBTTagCompound();

        nbt.setLong("Last", instance.getLastDeliveryIndex());
        nbt.setLong("Int", instance.getDeliveryInterval());
        nbt.setInteger("MaxSeq", instance.getMaxDeliverySequence());
        nbt.setInteger("Price", instance.getDeliveryPrice());

        final NBTTagList batchList = new NBTTagList();
        for (ISuppliesInvoice.Batch batch : instance.getBatches()) {
            final NBTTagCompound batchNbt = new NBTTagCompound();
            batchNbt.setDouble("Chance", batch.chance);
            ItemStackHelper.saveAllItems(batchNbt, batch.products);
            batchList.appendTag(batchNbt);
        }
        nbt.setTag("Batches", batchList);

        return nbt;
    }

    @Override
    public void readNBT(Capability<ISuppliesInvoice> capability, ISuppliesInvoice instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;
        if (nbt == null) return;

        instance.setLastDeliveryIndex(nbt.getLong("Last"));
        instance.setDeliveryInterval(nbt.getLong("Int"));
        instance.setMaxDeliverySequence(nbt.getInteger("MaxSeq"));
        instance.setDeliveryPrice(nbt.getInteger("Price"));

        final NBTTagList batchList = nbt.getTagList("Batches", 10);
        for (int i = 0; i < batchList.tagCount(); i++) {
            final NBTTagCompound batchNbt = batchList.getCompoundTagAt(i);
            final int productsSize = batchNbt.getTagList("Items", 10).tagCount();

            final ISuppliesInvoice.Batch batch = new ISuppliesInvoice.Batch();
            batch.chance = batchNbt.getDouble("Chance");
            batch.products = NonNullList.withSize(productsSize, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(batchNbt, batch.products);

            instance.addBatch(batch);
        }
    }
}
