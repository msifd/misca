package msifeed.misca.regions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandPest extends CommandTreeBase {
    @Override
    public String getName() {
        return "pest";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/pest <who append>";
    }

    public CommandPest() {
        addSubcommand(new Who());
        addSubcommand(new Append());
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
            if (args.length < 2) throw new CommandException("Usage: append <name> <class>");

            final Class<?> target;
            try {
                target = Class.forName(args[1]);
            } catch (ClassNotFoundException e) {
                throw new CommandException("Unknown entity class");
            }

            final int dim = sender.getEntityWorld().provider.getDimension();
            final String name = args[0];
            final RegionConfig.Region region = RegionControl.config().get(dim, name);
            if (region == null) throw new CommandException("Unknown region");

            if (region.blacklist.contains(target)) {
                sender.sendMessage(new TextComponentString("Region already contains this class"));
                return;
            }

            if (!region.blacklist.contains(target))
                region.blacklist.add(target);

            try {
                RegionControl.writeConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sender.sendMessage(new TextComponentString(String.format("Appended '%s' to region '%s'", target.getName(), name)));
        }
    }
}
