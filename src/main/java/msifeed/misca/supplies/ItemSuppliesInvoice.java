package msifeed.misca.supplies;

import msifeed.misca.Misca;
import msifeed.misca.supplies.cap.ISuppliesInvoice;
import msifeed.misca.supplies.cap.SuppliesInvoiceProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSuppliesInvoice extends Item {
    public static final String ID = "supplies_invoice";

    public ItemSuppliesInvoice() {
        setRegistryName(Misca.MODID, ID);
        setTranslationKey(ID);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return EnumActionResult.PASS;
        final TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof IInventory)) return EnumActionResult.FAIL;
        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(tile);
        if (supplies == null) return EnumActionResult.FAIL;

        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(player.getHeldItem(hand));
        if (invoice == null || invoice.isEmpty()) return EnumActionResult.FAIL;

        supplies.replaceWith(invoice);
        supplies.setLastDeliveryIndex(supplies.currentDeliveryIndex());
        player.sendStatusMessage(new TextComponentString("Supplies are set!"), true);

        return EnumActionResult.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(stack);
        if (invoice == null) {
            tooltip.add("Can't get supplies info");
            return;
        }

        tooltip.addAll(SuppliesFlow.getAbsoluteInfoLines(invoice));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        final SuppliesInvoiceProvider provider = new SuppliesInvoiceProvider();
        if (nbt != null) {
            provider.deserializeNBT(nbt.getTag("Invoice"));
        }
        return provider;
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(stack);
        final NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        nbt.setTag("Invoice", SuppliesInvoiceProvider.encode(invoice));
        return nbt;
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
        stack.setTagCompound(nbt);
        if (nbt != null) {
            final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(stack);
            SuppliesInvoiceProvider.CAP.readNBT(invoice, null, nbt.getTag("Invoice"));
        }
    }
}
