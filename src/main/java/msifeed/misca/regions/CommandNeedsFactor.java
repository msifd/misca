package msifeed.misca.regions;

import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.CharNeed;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandNeedsFactor extends CommandTreeBase {
    @Override
    public String getName() {
        return "needs";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "needs";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return MiscaPerms.check(sender, "misca.regions.needs");
    }

    public CommandNeedsFactor() {
        addSubcommand(new Append());
    }

    private static class Append extends CommandBase {
        @Override
        public String getName() {
            return "append";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "append <name> <need> <mod>";
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return MiscaPerms.check(sender, "misca.regions.needs.who");
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {

            switch (args.length) {
                case 1:
                    final int dim = sender.getEntityWorld().provider.getDimension();
                    return getListOfStringsMatchingLastWord(args, RegionControl.config().get(dim).stream()
                            .map(r -> r.name)
                            .collect(Collectors.toList()));
                case 2:
                    return getListOfStringsMatchingLastWord(args, Arrays.stream(CharNeed.values())
                            .map(CharNeed::toString)
                            .collect(Collectors.toList()));
                default:
                    return Collections.emptyList();
            }
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length < 3) throw new CommandException("Usage: append <name> <need> <mod>");

            final int dim = sender.getEntityWorld().provider.getDimension();
            final String name = args[0];

            final RegionConfig.Region region = RegionControl.config().get(dim, name);
            if (region == null) throw new CommandException("Unknown region");

            final CharNeed need = CharNeed.valueOf(args[1]);
            final double mod = parseDouble(args[2], -10, 10);
            region.needs.put(need, mod);

            try {
                RegionControl.writeConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sender.sendMessage(new TextComponentString(String.format("Appended %s %f to region '%s'", need, mod, name)));
        }
    }
}
