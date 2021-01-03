package msifeed.misca.supplies;

import msifeed.misca.Misca;
import msifeed.misca.MiscaThings;
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

import javax.annotation.Nullable;
import java.util.List;

public class ItemSuppliesInvoice extends Item {
    public static final String ID = "supplies_invoice";

    public ItemSuppliesInvoice() {
        setRegistryName(Misca.MODID, ID);
        setUnlocalizedName(ID);
    }

    public static ItemStack createInvoiceItem(ISuppliesInvoice invoice) {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Invoice", SuppliesInvoiceProvider.encode(invoice));

        final ItemStack item = new ItemStack(MiscaThings.suppliesInvoice);
        item.setTagCompound(nbt);

        return item;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return EnumActionResult.PASS;
        final TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof IInventory)) return EnumActionResult.FAIL;
        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(tile);
        if (supplies == null) return EnumActionResult.FAIL;

        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.decode(player.getHeldItem(hand).getOrCreateSubCompound("Invoice"));
        if (invoice.isEmpty()) return EnumActionResult.FAIL;

        supplies.replaceWith(invoice);
        player.sendStatusMessage(new TextComponentString("Supplies are set!"), true);

        return EnumActionResult.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        final ISuppliesInvoice tmpInvoice = SuppliesInvoiceProvider.decode(stack.getSubCompound("Invoice"));
        if (tmpInvoice == null || tmpInvoice.isEmpty()) {
            tooltip.add("Invoice is empty!: %d min");
            return;
        }

        tooltip.add(String.format("Interval: %d min", tmpInvoice.getDeliveryInterval() / 60000));
        tooltip.add("Max Sequence: " + tmpInvoice.getMaxDeliverySequence());

        tmpInvoice.getProducts().stream().limit(5).forEach(is -> {
            tooltip.add(String.format("* %d x %s", is.getCount(), is.getDisplayName()));
        });

        if (tmpInvoice.getProducts().size() > 5)
            tooltip.add(String.format("* and %d more", tmpInvoice.getProducts().size() - 5));
    }
}
