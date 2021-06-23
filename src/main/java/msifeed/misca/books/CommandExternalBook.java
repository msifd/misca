package msifeed.misca.books;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandExternalBook extends CommandBase {
    @Override
    public String getName() {
        return "externalBook";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/externalBook <index>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) return;
        if (args.length < 1) return;

        final EntityPlayer player = (EntityPlayer) sender;
        final String index = args[0];
        final ItemStack stack = ItemExternalBook.createStack(index);
        player.addItemStackToInventory(stack);
    }
}
