package msifeed.misca.cmd;

import msifeed.misca.MiscaPerms;
import msifeed.misca.rename.IRenameRpc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;

public class RenameCommand extends CommandBase {
    @Override
    public String getName() {
        return "rename";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/rename";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && MiscaPerms.userLevel(sender, "misca.rename");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        final EntityPlayerMP player = (EntityPlayerMP) sender;
        final ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (stack.isEmpty()) {
            sender.sendMessage(new TextComponentString("Take item in main hand!"));
            return;
        }

        IRenameRpc.openGui(player);
    }
}