package msifeed.misca.needs;

import msifeed.misca.needs.cap.IPlayerNeeds;
import msifeed.misca.needs.cap.PlayerNeedsProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class NeedsCommand extends CommandBase {
    @Override
    public String getName() {
        return "needs";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/needs <who> <integrity sanity stamina corruption> [<add set> <value>]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) throw new SyntaxErrorException("Expected at least 2 args");

        final EntityPlayerMP player = getPlayer(server, sender, args[0]);
        final IPlayerNeeds needs = PlayerNeedsProvider.get(player);
        final IPlayerNeeds.NeedType type = IPlayerNeeds.NeedType.valueOf(args[1]);

        if (args.length >= 4) {
            final boolean set = args[2].equalsIgnoreCase("set");
            final double value = (float) parseDouble(args[3], -type.max, type.max);
            if (set) needs.set(type, value);
            else needs.add(type, value);
        }

        final String rep = String.format("%s's %s: %.3f/%.0f", player.getDisplayNameString(), type.toString(), needs.get(type), type.max);
        sender.sendMessage(new TextComponentString(rep));
    }
}
