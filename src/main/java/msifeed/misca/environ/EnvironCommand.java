package msifeed.misca.environ;

import msifeed.misca.Misca;
import msifeed.misca.MiscaPerms;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EnvironCommand extends CommandBase {
    @Override
    public String getName() {
        return "environ";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/environ - for help";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return MiscaPerms.isGameMaster(sender);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args,"world", "rain");
            case 2:
                if (args[0].equals("rain"))
                    return getListOfStringsMatchingLastWord(args,"show", "toggle", "acc");
                return Collections.emptyList();
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        switch (args[0]) {
            case "world":
                worldList(server, sender);
                break;
            case "rain":
                handleRain(server, sender, args);
                break;
            default:
                sendHelp(sender);
                break;
        }
    }

    private void worldList(MinecraftServer server, ICommandSender sender) {
        sender.sendMessage(new TextComponentString(joinNiceStringFromCollection(
                Arrays.stream(server.worlds)
                        .map(EnvironCommand::worldInfo)
                        .collect(Collectors.toList()))));
        sender.sendMessage(new TextComponentString("Current: " + worldInfo(sender.getEntityWorld())));
    }

    private static String worldInfo(World w) {
        return String.format("%d - '%s'",
                w.provider.getDimension(), w.getWorldInfo().getWorldName());
    }

    private void handleRain(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 1) {
            sendHelp(sender);
            return;
        }

        final World world = sender.getEntityWorld();

        switch (args[1]) {
            case "show":
                showRain(sender, world);
                break;
            case "toggle":
                world.getWorldInfo().setRaining(!world.getWorldInfo().isRaining());
//                sender.sendMessage(new TextComponentString("Now: " + ()));
                break;
            case "acc":
                break;
            default:
                sendHelp(sender);
                break;
        }
    }

    private World getWorld(String worldName) throws CommandException {
        throw new CommandException("Unknown world: %s", worldName);
    }

    private void showRain(ICommandSender sender, World world) {
        final EnvironRule rule = Misca.getSharedConfig().environ.get(world.provider.getDimension());
        if (rule == null) {
            sender.sendMessage(new TextComponentString("No rules for this world"));
            return;
        }

        final EnvironWorldData data = EnvironWorldData.get(world);
        final EnvironRule.Rain r = rule.rain;

        final String format = "<Environ - Rain>\n" +
                "world: %s\n" +
                "acc: %d\n" +
                "in/out: +%d / -%d\n" +
                "min/max: %d / %d\n" +
                "thunder/dice: %d / %d\n";
        final String s = String.format(format, worldInfo(world), data.rainAcc, r.income, r.outcome, r.min, r.max, r.thunder, r.dice);
        sender.sendMessage(new TextComponentString(s));
    }

    private void toggleRain(World world) {
        final WorldInfo wi = world.getWorldInfo();
        wi.setRaining(!wi.isRaining());
    }

    private void sendHelp(ICommandSender sender) {
        final String s = "<Misca Environ>\n" +
                "Usage: /environ <category> <sub-command...>\n" +
                "Categories:\n" +
                "- world\n" +
                "-- list - list world names\n" +
                "- rain\n" +
                "-- show [world] - show rain values\n" +
                "-- toggle [world] - toggle rain\n" +
                "-- acc <delta> [world] - modify rain accumulator\n";
        sender.sendMessage(new TextComponentString(s));
    }
}
