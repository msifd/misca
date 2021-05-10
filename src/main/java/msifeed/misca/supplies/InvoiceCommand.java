package msifeed.misca.supplies;

import msifeed.misca.MiscaPerms;
import msifeed.misca.MiscaThings;
import msifeed.misca.charstate.handler.StaminaHandler;
import msifeed.misca.supplies.cap.ISuppliesInvoice;
import msifeed.misca.supplies.cap.SuppliesInvoiceProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class InvoiceCommand extends CommandBase {
    @Override
    public String getName() {
        return "invoice";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/invoice\n" +
                "  create <interval minutes> <max sequence> <stamina price>\n" +
                "  add [ chance (0;1] ]\n" +
                "  beacon\n" +
                "  OR point at chest without arguments";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return MiscaPerms.isGameMaster(sender);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, "create", "add", "beacon");
        else
            return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP))
            return;

        final EntityPlayerMP player = (EntityPlayerMP) sender;

        if (args.length == 0) {
            handleShowInfo(player);
        } else if (args[0].equals("beacon")) {
            handleCreateBeacon(player);
        } else if (args[0].equals("add")) {
            handleAddBatch(player, args);
        } else if (args[0].equals("pop")) {
            handlePopEntry(player);
        } else if (args.length >= 3) {
            handleCreateInvoice(player, args);
        } else {
            player.sendStatusMessage(new TextComponentString(getUsage(sender)), false);
        }
    }

    private void handleCreateInvoice(EntityPlayerMP player, String[] args) throws CommandException {
        final long interval = parseLong(args[1], 1, Long.MAX_VALUE);
        final int maxSequence = parseInt(args[2], 1, 100);
        final int price = parseInt(args[3], 1, 100);

        final ItemStack item = new ItemStack(MiscaThings.suppliesInvoice);
        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(item);
        if (invoice != null) {
            invoice.setDeliveryInterval(interval * 60000);
            invoice.setMaxDeliverySequence(maxSequence);
            invoice.setDeliveryPrice(price);
        }

        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, item);
    }

    private void handleAddBatch(EntityPlayerMP player, String[] args) throws CommandException {
        final ItemStack stack = player.inventory.getCurrentItem();
        if (stack.getItem() != MiscaThings.suppliesInvoice) {
            player.sendMessage(new TextComponentString("You need to take invoice in main hand!"));
            return;
        }

        final TileEntity tile = getPointingTile(player);
        if (tile == null) return;

        final IInventory container = (IInventory) tile;
        if (container.isEmpty()) {
            player.sendMessage(new TextComponentString("Container is empty!"));
            return;
        }

        final ISuppliesInvoice.Batch batch = new ISuppliesInvoice.Batch();
        if (args.length >= 2) {
            batch.chance = parseDouble(args[1]);
            if (batch.chance <= 0 || batch.chance > 1) {
                player.sendMessage(new TextComponentString("Batch chance must be int range (0;1]"));
                return;
            }
        }

        for (int i = 0; i < container.getSizeInventory(); i++) {
            final ItemStack is = container.getStackInSlot(i);
            if (!is.isEmpty())
                batch.products.add(is);
        }

        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(stack);
        if (invoice != null) {
            invoice.addBatch(batch);
        }

        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
    }

    private void handlePopEntry(EntityPlayerMP player) throws CommandException {
        final ItemStack stack = player.inventory.getCurrentItem();
        if (stack.getItem() != MiscaThings.suppliesInvoice) {
            player.sendMessage(new TextComponentString("You need to take invoice in main hand!"));
            return;
        }

        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(stack);
        if (invoice == null || invoice.getBatches().isEmpty()) return;

        invoice.getBatches().remove(invoice.getBatches().size() - 1);
        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
    }

    private static void handleCreateBeacon(EntityPlayerMP player) {
        final ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() != MiscaThings.suppliesInvoice) {
            player.sendMessage(new TextComponentString("You need to take invoice in main hand!"));
            return;
        }

        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(stack);
        if (invoice == null || invoice.isEmpty()) {
            player.sendMessage(new TextComponentString("This invoice is empty."));
            return;
        }

        final ItemStack beacon = ItemSuppliesBeacon.createBeaconItem(invoice);
        player.addItemStackToInventory(beacon);
    }

    private static void handleShowInfo(EntityPlayerMP player) {
        final TileEntity tile = getPointingTile(player);
        if (tile == null) return;

        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(tile);
        if (invoice == null) {
            player.sendMessage(new TextComponentString("Supplies capability is somehow missing! :("));
            return;
        }

        if (invoice.isEmpty()) {
            player.sendMessage(new TextComponentString("No supplies"));
            return;
        }

        final double stamina = StaminaHandler.getStaminaForDelivery(player);
        player.sendMessage(new TextComponentString("=== Supplies ==="));
        SuppliesFlow.getRelativeInfoLines(invoice, stamina).stream()
                .map(TextComponentString::new)
                .forEach(player::sendMessage);
    }

    @Nullable
    private static TileEntity getPointingTile(EntityPlayerMP player) {
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
