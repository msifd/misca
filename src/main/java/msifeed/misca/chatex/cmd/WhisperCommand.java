package msifeed.misca.chatex.cmd;

import msifeed.misca.MiscaConfig;
import msifeed.misca.chatex.IChatexRpc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class WhisperCommand extends CommandBase {
    @Override
    public String getName() {
        return "whisper";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/whisper <text>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) return;

        final int range = MiscaConfig.chat.getSpeechRange(-2);
        final String text = String.join(" ", args);

        IChatexRpc.sendSpeech((EntityPlayerMP) sender, range, text);
    }
}
