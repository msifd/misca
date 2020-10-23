package msifeed.misca.environ;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.Arrays;
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
                Arrays.stream(server.worlds).map(w -> w.getWorldInfo().getWorldName()).collect(Collectors.toList()))));
        sender.sendMessage(new TextComponentString("Current: " + sender.getEntityWorld().getWorldInfo().getWorldName()));
    }

    private void handleRain(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 1) {
            sendHelp(sender);
            return;
        }

        final World world = sender.getEntityWorld();

        switch (args[1]) {
            case "show":
                break;
            case "toggle":

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

    private void showRain(World world) {

    }

    private void toggleRain(World world) {
        final WorldInfo wi = world.getWorldInfo();
        wi.setRaining(!wi.isRaining());
    }

    private void sendHelp(ICommandSender sender) {
        String s = "<Misca Environ>\n" +
                "Usage: /environ <category> <sub-command...>" +
                "Categories:" +
                "world\n" +
                "- list - list world names\n" +
                "rain\n" +
                "- show [world] - show rain values\n" +
                "- toggle [world] - toggle rain\n" +
                "- acc <delta> [world] - modify rain accumulator\n";
        sender.sendMessage(new TextComponentString(s));
    }
}
