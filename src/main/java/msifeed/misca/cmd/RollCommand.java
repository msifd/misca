package msifeed.misca.cmd;

import msifeed.misca.chatex.ChatexRpc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollCommand extends CommandBase {
    private final Pattern pattern = Pattern.compile("(\\d*)D(\\d*)([+-]\\d+)?", Pattern.CASE_INSENSITIVE);
    private final Random random = new Random();

    @Override
    public String getName() {
        return "roll";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/roll 3d7+5";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        final String joined = String.join(" ", args);
        final Matcher matcher = pattern.matcher(joined);
        if (!matcher.matches()) {
            sender.sendMessage(new TextComponentString("invalid input"));
            return;
        }

        final String rawDiceCount = matcher.group(1);
        final String rawSide = matcher.group(2);
        final String rawModifier = matcher.group(3);

        final int diceCount;
        final int side;
        final int modifier;
        try {
            diceCount = rawDiceCount != null && !rawDiceCount.isEmpty() ? parseInt(rawDiceCount) : 1;
            side = rawSide != null && !rawSide.isEmpty() ? parseInt(rawSide) : 20;
            modifier = rawModifier != null && !rawModifier.isEmpty() ? parseInt(rawModifier) : 0;
        } catch (NumberInvalidException e) {
            sender.sendMessage(new TextComponentString("invalid input"));
            return;
        }

        long result = 0;
        for (int i = 0; i < diceCount; i++)
            result += random.nextInt(side) + 1;
        result += modifier;

        if (sender instanceof EntityPlayerMP) {
            ChatexRpc.broadcastRoll((EntityPlayerMP) sender, joined, result);
        } else {
            final String name = sender.getDisplayName().getFormattedText();
            final String msg = String.format("[ROLL] %s: %s = %d", name, joined, result);
            sender.sendMessage(new TextComponentString(msg));
        }
    }
}
