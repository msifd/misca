package msifeed.misca.chatex.cmd;

import com.google.common.collect.ImmutableList;
import msifeed.misca.chatex.ChatexRpc;
import msifeed.misca.chatex.ChatexUtils;
import msifeed.misca.chatex.GameMasterParams;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GmSayCommand extends CommandBase {
    private static final List<String> SUBCOMMANDS = ImmutableList.of("!speech", "!range", "!color", "!prefix");
    private static final List<String> COLORS = Stream.of(TextFormatting.values())
            .filter(TextFormatting::isColor)
            .map(TextFormatting::getFriendlyName)
            .collect(Collectors.toList());

    @Override
    public String getName() {
        return "gms";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/gms";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, SUBCOMMANDS);
            case 2:
                return args[0].equals("!color") ? getListOfStringsMatchingLastWord(args, COLORS) : Collections.emptyList();
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        final EntityPlayerMP player = (EntityPlayerMP) sender;
        final GameMasterParams.Entry params = GameMasterParams.INSTANCE.getOrCreate(player.getUniqueID());

        if (args.length == 0) {
            printStatus(sender, params);
        } else if (args[0].startsWith("!")) {
            handleConfig(sender, args, params);
        } else {
            final String text = String.join(" ", args);
            ChatexRpc.broadcastGameMasterSay(player, params.range, params.format(text));
        }
    }

    private void handleConfig(ICommandSender sender, String[] args, GameMasterParams.Entry params) throws CommandException {
        switch (args[0]) {
            case "!speech":
                params.replaceSpeech = !params.replaceSpeech;
                sender.sendMessage(new TextComponentString("[GmSay] Replace speech: " + params.replaceSpeech));
                return;
            case "!range":
                if (args.length >= 2) {
                    params.range = parseInt(args[1], 0, 10000);
                    sender.sendMessage(new TextComponentString("[GmSay] Range: " + params.range));
                    return;
                }
                break;
            case "!color":
                if (args.length >= 2) {
                    final TextFormatting color = TextFormatting.getValueByName(args[1]);
                    if (color != null && color.isColor()) {
                        params.color = color;
                    }
                }
                break;
            case "!prefix":
                if (args.length >= 2) {
                    final String text = Stream.of(args).skip(1).collect(Collectors.joining(" "));
                    params.prefix = text.replace('&', '\u00a7');
                }
                break;
        }

        printStatus(sender, params);
    }

    private void printStatus(ICommandSender sender, GameMasterParams.Entry params) {
        final String s = TextFormatting.BLUE.toString() + "[GmSay]\n" + TextFormatting.RESET.toString()
                + " Replace speech: " + params.replaceSpeech
                + ", Range: " + params.range
                + ", Color: " + params.color.toString() + params.color.getFriendlyName() + " " + params.color.getColorIndex() + TextFormatting.RESET.toString()
                + "\n Prefix: " + ChatexUtils.intoAmpersandFormat(params.prefix);

        sender.sendMessage(new TextComponentString(s));
        sender.sendMessage(params.format("Example text."));
    }
}
