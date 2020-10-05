package msifeed.misca.supplies;

import msifeed.misca.Misca;
import msifeed.misca.MiscaThings;
import msifeed.misca.genesis.blocks.tiles.TileEntityGenesisContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

    private final transient SuppliesInvoice tmpInvoice = new SuppliesInvoice();

    public ItemSuppliesInvoice() {
        setRegistryName(Misca.MODID, ID);
        setUnlocalizedName(ID);
    }

    public static ItemStack createInvoiceItem(SuppliesInvoice invoice) {
        final ItemStack item = new ItemStack(MiscaThings.invoice);
        invoice.writeToNBT(item.getOrCreateSubCompound("Invoice"));
        return item;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        final TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileEntityGenesisContainer))
            return EnumActionResult.FAIL;

        final SuppliesInvoice invoice = new SuppliesInvoice();
        invoice.readFromNBT(player.getHeldItem(hand).getOrCreateSubCompound("Invoice"));

        final TileEntityGenesisContainer container = (TileEntityGenesisContainer) tile;
        container.setInvoice(invoice);

        player.sendStatusMessage(new TextComponentString("Supplies are set!"), true);
        return EnumActionResult.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tmpInvoice.readFromNBT(stack.getOrCreateSubCompound("Invoice"));

        tooltip.add(String.format("Interval: %d min", tmpInvoice.interval / 60000));
        tooltip.add("Max Sequence: " + tmpInvoice.maxSequence);

        tmpInvoice.products.stream().limit(5).forEach(is -> {
            tooltip.add(String.format("* %d x %s", is.getCount(), is.getDisplayName()));
        });

        if (tmpInvoice.products.size() > 5)
            tooltip.add(String.format("* and %d more", tmpInvoice.products.size() - 5));
    }
}
