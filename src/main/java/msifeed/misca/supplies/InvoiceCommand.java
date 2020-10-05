package msifeed.misca.supplies;

import msifeed.misca.genesis.blocks.tiles.TileEntityGenesisContainer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
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
        if (!(tile instanceof TileEntityGenesisContainer)) {
            sender.sendMessage(new TextComponentString("This is not a container!"));
            return;
        }

        final TileEntityGenesisContainer container = (TileEntityGenesisContainer) tile;

        if (args.length == 0) {
            final SuppliesInvoice invoice = container.getInvoice();
            if (invoice == null) {
                sender.sendMessage(new TextComponentString("Container has no supplies."));
                return;
            }

            sender.sendMessage(new TextComponentString("=== Supplies ==="));
            sender.sendMessage(new TextComponentString(
                    String.format("Interval: %d min, Max Sequence: %d", invoice.interval / 60000, invoice.maxSequence)));
            for (ItemStack is : invoice.products)
                sender.sendMessage(new TextComponentString(String.format("* %d x %s", is.getCount(), is.getDisplayName())));

            return;
        }

        if (args.length != 2) {
            sender.sendMessage(new TextComponentString("Look at Misca container and call command."));
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        final long interval = parseLong(args[0], 1, Long.MAX_VALUE);
        final int maxSequence = parseInt(args[1], 1, 1000);

        if (container.isEmpty()) {
            sender.sendMessage(new TextComponentString("Container is empty!"));
            return;
        }

        final SuppliesInvoice invoice = new SuppliesInvoice();
        invoice.interval = interval * 60000;
        invoice.maxSequence = maxSequence;
        for (ItemStack is : container.getItems()) {
            if (!is.isEmpty())
                invoice.products.add(is);
        }

        player.addItemStackToInventory(ItemSuppliesInvoice.createInvoiceItem(invoice));
    }
}
