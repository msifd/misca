package msifeed.misca.regions;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandRegions extends CommandTreeBase {
    @Override
    public String getName() {
        return "regions";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/regions <get pest needs>";
    }

    public CommandRegions() {
        addSubcommand(new Get());
        addSubcommand(new Add());
        addSubcommand(new Delete());
        addSubcommand(new CommandPest());
        addSubcommand(new CommandNeedsFactor());
    }

    private static class Get extends CommandBase {
        @Override
        public String getName() {
            return "get";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "get";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            final List<RegionConfig.Region> regions = RegionControl.getLocalRules(sender.getEntityWorld(), sender.getPositionVector())
                    .collect(Collectors.toList());

            if (regions.isEmpty()) {
                sender.sendMessage(new TextComponentString("No regions here"));
                return;
            }

            for (RegionConfig.Region r : regions) {
                final String needs = r.needs.entrySet().stream()
                        .map(e -> e.getKey() + ":" + e.getValue())
                        .collect(Collectors.joining(", "));
                final String blacklist = r.blacklist.stream()
                        .map(Class::getName)
                        .collect(Collectors.joining(", "));
                final String whitelist = r.whitelist.stream()
                        .map(Class::getName)
                        .collect(Collectors.joining(", "));
                final String msg = r.name + ":\n" +
                        "  needs: " + needs + '\n' +
                        "  blacklist: " + blacklist + '\n' +
                        "  whitelist: " + whitelist;
                sender.sendMessage(new TextComponentString(msg));
            }
        }
    }

    private static class Add extends CommandBase {
        @Override
        public String getName() {
            return "add";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "add <name> [global]";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length < 1) throw new CommandException("Usage: add <name> [global]");

            final int dim = sender.getEntityWorld().provider.getDimension();
            final String name = args[0];

            final AxisAlignedBB aabb;
            if (args.length >= 2 && args[1].equals("global"))
                aabb = null;
            else
                aabb = getRegion(sender);

            if (RegionControl.config().get(dim, name) != null)
                throw new CommandException("Region with this name already exists.");

            final RegionConfig.Region region = new RegionConfig.Region();
            region.name = name;
            region.aabb = aabb;

            RegionControl.config().add(dim, region);

            try {
                RegionControl.writeConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sender.sendMessage(new TextComponentString(String.format("Added new region '%s'", name)));
        }
    }

    private static class Delete extends CommandBase {
        @Override
        public String getName() {
            return "delete";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "delete <name>";
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            if (args.length == 1) {
                final int dim = sender.getEntityWorld().provider.getDimension();
                return RegionControl.config().get(dim).stream()
                        .map(r -> r.name)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length < 1) throw new CommandException("Usage: delete <name>");

            final int dim = sender.getEntityWorld().provider.getDimension();
            final String name = args[0];
            RegionControl.config().delete(dim, name);

            try {
                RegionControl.writeConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sender.sendMessage(new TextComponentString(String.format("Removed all regions with name '%s'", name)));
        }
    }

    private static AxisAlignedBB getRegion(ICommandSender sender) throws CommandException {
        final SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        final LocalSession session = sessionManager.findByName(sender.getName());
        if (session == null) throw new CommandException("Can't find your WorldEdit session");

        final World selectionWorld = session.getSelectionWorld();
        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            final Region region = session.getSelection(selectionWorld);
            return new AxisAlignedBB(
                    region.getMinimumPoint().getX(),
                    region.getMinimumPoint().getY(),
                    region.getMinimumPoint().getZ(),
                    region.getMaximumPoint().getX(),
                    region.getMaximumPoint().getY(),
                    region.getMaximumPoint().getZ()
            );
        } catch (IncompleteRegionException e) {
            throw new CommandException("Please make a region selection first.");
        }
    }
}
