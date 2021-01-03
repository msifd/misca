package msifeed.misca.supplies;

import msifeed.misca.MiscaThings;
import msifeed.misca.supplies.cap.ISuppliesInvoice;
import msifeed.misca.supplies.cap.SuppliesInvoice;
import msifeed.misca.supplies.cap.SuppliesInvoiceProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

public class InvoiceCommand extends CommandBase {
    @Override
    public String getName() {
        return "invoice";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/invoice <interval minutes> <max sequence> OR /invoice beacon OR without arguments";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer))
            return;

        final EntityPlayer player = (EntityPlayer) sender;

        if (args.length == 0) {
            handleShowInfo(player);
        } else if (args[0].equals("beacon")) {
            handleCreateBeacon(player);
        } else if (args.length >= 2) {
            handleCreateInvoice(player, args);
        } else {
            player.sendStatusMessage(new TextComponentString(getUsage(sender)), false);
        }
    }

    private static void handleCreateBeacon(EntityPlayer player) {
        final ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() != MiscaThings.suppliesInvoice) {
            player.sendMessage(new TextComponentString("You need to take invoice in main hand!"));
            return;
        }

        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.decode(stack.getOrCreateSubCompound("Invoice"));
        if (invoice.isEmpty()) {
            player.sendMessage(new TextComponentString("This invoice is empty."));
            return;
        }

        final ItemStack beacon = ItemSuppliesBeacon.createBeaconItem(invoice);
        if (beacon == null) {
            player.sendMessage(new TextComponentString("Cant create beacon from this invoice, somehow."));
            return;
        }

        player.addItemStackToInventory(beacon);

    }

    private void handleCreateInvoice(EntityPlayer player, String[] args) throws CommandException {
        final TileEntity tile = getPointingTile(player);
        if (tile == null) return;

        final IInventory container = (IInventory) tile;
        if (container.isEmpty()) {
            player.sendMessage(new TextComponentString("Container is empty!"));
            return;
        }

        final long interval = parseLong(args[0], 1, Long.MAX_VALUE);
        final int maxSequence = parseInt(args[1], 1, 1000);

        final ISuppliesInvoice invoice = new SuppliesInvoice();
        invoice.setDeliveryInterval(interval * 60000);
        invoice.setMaxDeliverySequence(maxSequence);

        final NonNullList<ItemStack> products = invoice.getProducts();
        for (int i = 0; i < container.getSizeInventory(); i++) {
            final ItemStack is = container.getStackInSlot(i);
            if (!is.isEmpty())
                products.add(is);
        }

        player.addItemStackToInventory(ItemSuppliesInvoice.createInvoiceItem(invoice));
    }

    private static void handleShowInfo(EntityPlayer player) {
        final TileEntity tile = getPointingTile(player);
        if (tile == null) return;

        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(tile);
        if (supplies == null) {
            player.sendMessage(new TextComponentString("Supplies capability is somehow missing! :("));
            return;
        }

        player.sendMessage(new TextComponentString("=== Supplies ==="));
        if (!supplies.isEmpty()) {
            final long interval = supplies.getDeliveryInterval();
            final long lastDelivery = supplies.getLastDeliveryTime();
            final long maxSequence = supplies.getMaxDeliverySequence();
            final long now = System.currentTimeMillis();
            final double nextSupply = (interval - (now - lastDelivery) % interval) / 60000d;

            player.sendMessage(new TextComponentString(String.format("Interval: %d min, Max Sequence: %d",
                    interval / 60000, maxSequence)));
            player.sendMessage(new TextComponentString(String.format("Next supply in: %.1f min", nextSupply)));
            for (ItemStack is : supplies.getProducts()) {
                player.sendMessage(new TextComponentString(String.format("* %d x %s",
                        is.getCount(), is.getDisplayName())));
            }
        } else {
            player.sendMessage(new TextComponentString("No supplies!"));
        }
    }

    @Nullable
    private static TileEntity getPointingTile(EntityPlayer player) {
        final RayTraceResult ray = ForgeHooks.rayTraceEyes(player, 5);
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK) {
            player.sendMessage(new TextComponentString("Look at container!"));
            return null;
        }

        final TileEntity tile = player.world.getTileEntity(ray.getBlockPos());
        if (!(tile instanceof IInventory)) {
            player.sendMessage(new TextComponentString("This is not a container!"));
            return null;
        }

        return tile;
    }
}
