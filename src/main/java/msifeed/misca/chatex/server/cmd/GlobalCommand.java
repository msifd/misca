package msifeed.misca.chatex.server.cmd;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class GlobalCommand extends CommandBase {
    @Override
    public String getName() {
        return "g";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/g <global text>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        sender.sendMessage(new TextComponentString("u sent global msg"));
    }
}
