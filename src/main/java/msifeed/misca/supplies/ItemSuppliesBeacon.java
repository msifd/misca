package msifeed.misca.supplies;

import msifeed.misca.Misca;
import msifeed.misca.MiscaThings;
import msifeed.misca.supplies.cap.ISuppliesInvoice;
import msifeed.misca.supplies.cap.SuppliesInvoiceProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSuppliesBeacon extends Item {
    public static final String ID = "supplies_beacon";

    public ItemSuppliesBeacon() {
        setRegistryName(Misca.MODID, ID);
        setTranslationKey(ID);
    }

    public static ItemStack createBeaconItem(ISuppliesInvoice invoice) {
        final ItemStack beacon = new ItemStack(MiscaThings.suppliesBeacon);
        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(beacon);
        if (supplies != null) {
            supplies.replaceWith(invoice);
            supplies.setLastDeliveryIndex(supplies.currentDeliveryIndex());
        }
        return beacon;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        final ItemStack stack = player.getHeldItem(hand);

        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(stack);
        if (invoice == null) return new ActionResult<>(EnumActionResult.FAIL, stack);

        final long deliveryIndex = invoice.getLastDeliveryIndex();
        final IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        final int maxOverride = player.isSneaking() ? 1 : Integer.MAX_VALUE;

        if (world.isRemote) {
            final int times = SuppliesFlow.getDeliveryTimes(player, invoice, maxOverride);
            SuppliesFlow.updateDeliveryIndex(invoice, times);
        } else {
            SuppliesFlow.makeDelivery(player, invoice, inv, maxOverride);
        }

        final EnumActionResult result = deliveryIndex != invoice.getLastDeliveryIndex()
                ? EnumActionResult.SUCCESS
                : EnumActionResult.FAIL;

        return new ActionResult<>(result, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(stack);
        if (invoice == null) {
            tooltip.add("Can't get supplies info");
            return;
        }

        tooltip.addAll(SuppliesFlow.getRelativeInfoLines(invoice));
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
