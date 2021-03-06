package msifeed.misca.cmd;

import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charstate.cap.CharstateProvider;
import msifeed.sys.cap.FloatContainer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EffortsCommand extends CommandBase {
    private static final Set<String> effortNames = Stream.of(CharEffort.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    @Override
    public String getName() {
        return "efforts";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/efforts <who> <effort> [<add set> <value>]";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            case 2:
                return getListOfStringsMatchingLastWord(args, effortNames);
            case 3:
                return getListOfStringsMatchingLastWord(args,"add", "set");
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) throw new SyntaxErrorException("Expected at least 2 args");

        final EntityPlayerMP player = getPlayer(server, sender, args[0]);
        final CharEffort eff = getEffort(args[1]);

        final FloatContainer<CharEffort> efforts = CharstateProvider.get(player).efforts();
        final int max = CharsheetProvider.get(player).effortPools().get(eff);

        if (args.length >= 4 && MiscaPerms.isGameMaster(sender)) {
            final boolean set = args[2].equalsIgnoreCase("set");
            final float value = (float) parseDouble(args[3], -50, 50);
            if (set) efforts.set(eff, MathHelper.clamp(value, 0, max));
            else efforts.set(eff, MathHelper.clamp(value + efforts.get(eff), 0, max));
        }

        final String rep = String.format("%s's %s: %.2f/%d", player.getDisplayNameString(), eff.name(), efforts.get(eff), max);
        sender.sendMessage(new TextComponentString(rep));
    }

    private static CharEffort getEffort(String name) throws CommandException {
        switch (name) {
            case "impact":
                return CharEffort.impact;
            case "knowledge":
                return CharEffort.knowledge;
            case "reflection":
                return CharEffort.reflection;
            case "confidence":
                return CharEffort.confidence;
            case "reputation":
                return CharEffort.reputation;
            default:
                throw new SyntaxErrorException("Invalid effort name");
        }
    }
}
