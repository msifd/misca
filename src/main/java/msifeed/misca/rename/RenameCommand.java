package msifeed.misca.rename;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
        return sender instanceof EntityPlayerMP;
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
