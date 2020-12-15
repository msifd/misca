package msifeed.misca.chatex.cmd;

import msifeed.misca.Misca;
import msifeed.misca.chatex.ChatexRpc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class YellCommand extends CommandBase {
    @Override
    public String getName() {
        return "yell";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/yell <text>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) return;

        final int range = Misca.getSharedConfig().chat.getSpeechRange(+1);
        final String text = String.join(" ", args);

        ChatexRpc.sendSpeech((EntityPlayerMP) sender, range, text);
    }
}
