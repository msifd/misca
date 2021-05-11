package msifeed.misca.pest;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CommandPest extends CommandTreeBase {
    @Override
    public String getName() {
        return "pest";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/pest";
    }

    public CommandPest() {
        addSubcommand(new Who());
        addSubcommand(new Here());
        addSubcommand(new Add());
        addSubcommand(new Append());
        addSubcommand(new Delete());
    }

    private static class Who extends CommandBase {
        @Override
        public String getName() {
            return "who";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "who";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            final AxisAlignedBB aabb = new AxisAlignedBB(sender.getPosition()).grow(10);

            final List<String> classes = sender.getEntityWorld().getLoadedEntityList().stream()
                    .filter(e -> e instanceof EntityLivingBase && !(e instanceof EntityPlayer))
                    .filter(e -> aabb.contains(e.getPositionVector()))
                    .map(Entity::getClass)
                    .distinct()
                    .map(Class::getName)
                    .collect(Collectors.toList());
            if (classes.isEmpty()) {
                sender.sendMessage(new TextComponentString("No one here"));
                return;
            }

            for (String s : classes) {
                final ITextComponent tc = new TextComponentString(s);
                tc.getStyle().setInsertion(s);
                sender.sendMessage(tc);
            }
        }
    }

    private static class Here extends CommandBase {
        @Override
        public String getName() {
            return "here";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "here";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            final List<PestConfig.Rule> rules = PestControl.getLocalRules(sender.getEntityWorld(), sender.getPositionVector());
            if (rules.isEmpty()) {
                sender.sendMessage(new TextComponentString("No pest rules here"));
                return;
            }

            for (PestConfig.Rule r : rules) {
                final String classes = r.classes.stream()
                        .map(Class::getName)
                        .collect(Collectors.joining(", "));
                sender.sendMessage(new TextComponentString(String.format("'%s': [%s]", r.name, classes)));
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
            return "add <name> <class> [global]";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length < 2) throw new CommandException("Usage: add <name> <class> [global]");

            final Class<?> target;
            try {
                target = Class.forName(args[1]);
            } catch (ClassNotFoundException e) {
                throw new CommandException("Unknown entity class");
            }

            final AxisAlignedBB aabb;
            if (args.length >= 3 && args[2].equals("global")) {
                aabb = null;
            } else {
                final LocalSession session = getSession(sender);
                final Region region = getRegion(session);
                aabb = new AxisAlignedBB(
                        region.getMinimumPoint().getX(),
                        region.getMinimumPoint().getY(),
                        region.getMinimumPoint().getZ(),
                        region.getMaximumPoint().getX(),
                        region.getMaximumPoint().getY(),
                        region.getMaximumPoint().getZ()
                );
            }

            final int dim = sender.getEntityWorld().provider.getDimension();
            final String name = args[0];
            final PestConfig.Rule rule = new PestConfig.Rule();
            rule.name = name;
            rule.classes.add(target);
            rule.aabb = aabb;

            PestControl.config().add(dim, rule);

            try {
                PestControl.writeConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sender.sendMessage(new TextComponentString(String.format("Added new rule '%s' with '%s'", name, target.getName())));
        }
    }

    private static class Append extends CommandBase {
        @Override
        public String getName() {
            return "append";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "append <name> <class>";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length < 2) throw new CommandException("Usage: append <name> <class>");

            final Class<?> target;
            try {
                target = Class.forName(args[1]);
            } catch (ClassNotFoundException e) {
                throw new CommandException("Unknown entity class");
            }

            final int dim = sender.getEntityWorld().provider.getDimension();
            final String name = args[0];
            final PestConfig.Rule rule = PestControl.config().get(dim, name);
            if (rule == null) throw new CommandException("Unknown rule");

            if (!rule.classes.contains(target))
                rule.classes.add(target);

            try {
                PestControl.writeConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sender.sendMessage(new TextComponentString(String.format("Appended '%s' to rule '%s'", target.getName(), name)));
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
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length < 1) throw new CommandException("Usage: delete <name>");

            final int dim = sender.getEntityWorld().provider.getDimension();
            final String name = args[0];
            PestControl.config().delete(dim, name);

            try {
                PestControl.writeConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sender.sendMessage(new TextComponentString(String.format("Removed all rules with name '%s'", name)));
        }
    }

    private static LocalSession getSession(ICommandSender sender) throws CommandException {
        final SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        final LocalSession session = sessionManager.findByName(sender.getName());
        if (session == null) throw new CommandException("Can't find your WorldEdit session");

        return session;
    }

    private static Region getRegion(LocalSession session) throws CommandException {
        final World selectionWorld = session.getSelectionWorld();

        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            return session.getSelection(selectionWorld);
        } catch (IncompleteRegionException e) {
            throw new CommandException("Please make a region selection first.");
        }
    }
}
