package msifeed.misca.supplies;

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

public class InvoiceCommand extends CommandBase {
    @Override
    public String getName() {
        return "invoice";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/invoice <interval minutes> <max sequence> OR without arguments";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer))
            return;

        final EntityPlayer player = (EntityPlayer) sender;
        final RayTraceResult ray = ForgeHooks.rayTraceEyes(player, 5);
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK) {
            sender.sendMessage(new TextComponentString("Look at container!"));
            return;
        }

        final TileEntity tile = player.world.getTileEntity(ray.getBlockPos());
        if (!(tile instanceof IInventory)) {
            sender.sendMessage(new TextComponentString("This is not a container!"));
            return;
        }
        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(tile);
        if (supplies == null) {
            sender.sendMessage(new TextComponentString("Supplies capability is somehow missing! :("));
            return;
        }


        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("=== Supplies ==="));
            if (!supplies.isEmpty()) {
                final long interval = supplies.getDeliveryInterval();
                final long lastDelivery = supplies.getLastDeliveryTime();
                final long maxSequence = supplies.getMaxDeliverySequence();
                final long now = System.currentTimeMillis();
                final double nextSupply = (now - lastDelivery) % interval / (double) interval;

                sender.sendMessage(new TextComponentString(String.format("Interval: %d min, Max Sequence: %d",
                        interval / 60000, maxSequence)));
                sender.sendMessage(new TextComponentString(String.format("Next supply in: %.1f min", nextSupply)));
                for (ItemStack is : supplies.getProducts()) {
                    sender.sendMessage(new TextComponentString(String.format("* %d x %s",
                            is.getCount(), is.getDisplayName())));
                }
            } else {
                sender.sendMessage(new TextComponentString("No supplies!"));
            }
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(new TextComponentString("Look at Misca container and call command."));
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        final IInventory container = (IInventory) tile;
        if (container.isEmpty()) {
            sender.sendMessage(new TextComponentString("Container is empty!"));
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
}
