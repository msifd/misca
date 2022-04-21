package msifeed.misca.cmd;

import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CharsheetCommand extends CommandBase {
    @Override
    public String getName() {
        return "charsheet";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/charsheet <who>";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());

        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) throw new WrongUsageException("Expected at least 2 args");

        final EntityPlayerMP player = getPlayer(server, sender, args[0]);
        dump(sender, player);
    }

    private void dump(ICommandSender sender, EntityPlayerMP player) {
        final ICharsheet sheet = CharsheetProvider.get(player);
        sender.sendMessage(new TextComponentString("Dump: " + player.getName() + " / " + sheet.getName()));
        sender.sendMessage(new TextComponentString(
                "  Skills: " + sheet.skills().stream()
                        .filter(e -> e.getValue() != 0)
                        .map(e -> e.getKey().tr() + ' ' + e.getValue())
                        .collect(Collectors.joining(", "))));
        sender.sendMessage(new TextComponentString(
                "  Potions: " + sheet.potions().entrySet().stream()
                        .map(e -> e.getKey().getName().replace("effect.", "") + ' ' + e.getValue())
                        .collect(Collectors.joining(", "))));
        sender.sendMessage(new TextComponentString(
                "  Enchants: " + sheet.enchants().entrySet().stream()
                        .map(e -> e.getKey().getTranslatedName(e.getValue()))
                        .collect(Collectors.joining(", "))));
    }
}
