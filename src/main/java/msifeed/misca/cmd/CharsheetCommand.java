package msifeed.misca.cmd;

import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.CharNeed;
import msifeed.misca.charsheet.CharResource;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.CharsheetSync;
import msifeed.misca.logdb.LogDB;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CharsheetCommand extends CommandBase {
    @Override
    public String getName() {
        return "charsheet";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/charsheet <who> <dump factor ord seal> [<add set> <value>]";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            case 2:
                return getListOfStringsMatchingLastWord(args, "dump", "factor", "ord", "seal");
            case 3:
                if (args[1].equals("ord") || args[1].equals("seal"))
                    return getListOfStringsMatchingLastWord(args, "add", "set");
                else if (args[1].equals("factor"))
                    return getListOfStringsMatchingLastWord(args, "cor", "int", "san", "sta");
                else
                    return Collections.emptyList();
            case 4:
                if (args[1].equals("factor"))
                    return getListOfStringsMatchingLastWord(args, "rest", "lost");
                else
                    return Collections.emptyList();
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) throw new WrongUsageException("Expected at least 2 args");

        final EntityPlayerMP player = getPlayer(server, sender, args[0]);
        if (args[1].equals("dump")) {
            dump(sender, player);
        } else if (args[1].equals("factor")) {
            modifyFactor(sender, player, args);
        } else {
            modifyRes(sender, player, args);
        }
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
                "  Effort pools: " + sheet.effortPools().stream()
                        .filter(e -> e.getValue() != 0)
                        .map(e -> e.getKey().tr() + ' ' + e.getValue())
                        .collect(Collectors.joining(", "))));
        sender.sendMessage(new TextComponentString(
                "  Resources: " + sheet.resources().stream()
                        .map(e -> e.getKey().toString() + ' ' + e.getValue())
                        .collect(Collectors.joining(", "))));
        sender.sendMessage(new TextComponentString(
                "  Need factors: " + Stream.of(CharNeed.values())
                        .map(n -> n.toString().toLowerCase() + " +" + sheet.needsGain().get(n) + " -" + sheet.needsLost().get(n))
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

    private void modifyFactor(ICommandSender sender, EntityPlayerMP player, String[] args) throws CommandException {
        if (!MiscaPerms.isGameMaster(sender)) throw new WrongUsageException("You are not GM");
        if (args.length < 5) throw new WrongUsageException("Expected at least 4 args");

        final CharNeed need = CharNeed.valueOf(args[2].toUpperCase());
        final float value = (float) parseDouble(args[4], 0, 10);

        final boolean isRest;
        if (args[3].equals("rest")) isRest = true;
        else if (args[3].equals("lost")) isRest = false;
        else throw new WrongUsageException("Arg 4 must be 'rest' or 'lost'");

        final ICharsheet sheet = CharsheetProvider.get(player);

        final float curr;
        final String msg;
        if (isRest) {
            curr = sheet.needsGain().get(need);
            msg = String.format("Change %s's %s rest factor from %f to %f", player.getName(), need, curr, value);
            sheet.needsGain().set(need, value);
        } else {
            curr = sheet.needsLost().get(need);
            msg = String.format("Change %s's %s lost factor from %f to %f", player.getName(), need, curr, value);
            sheet.needsLost().set(need, value);
        }

        CharsheetSync.sync(player);

        sender.sendMessage(new TextComponentString(msg));
        LogDB.INSTANCE.log(sender, "charsheet", msg);
    }

    private void modifyRes(ICommandSender sender, EntityPlayerMP player, String[] args) throws CommandException {
        final ICharsheet sheet = CharsheetProvider.get(player);

        final CharResource res;
        if (args[1].equals("ord")) res = CharResource.ord;
        else if (args[1].equals("seal")) res = CharResource.seal;
        else throw new SyntaxErrorException("Unknown resource: " + args[1]);

        if (args.length >= 4 && MiscaPerms.isGameMaster(sender)) {
            final boolean set = args[2].equalsIgnoreCase("set");
            final int value = parseInt(args[3], 0, 100);
            final int curr = sheet.resources().get(res);
            final int modified = set ? value : value + curr;

            sheet.resources().set(res, modified);
            CharsheetSync.sync(player);

            final String msg = String.format("Change %s's %s from %d to %d", player.getName(), res, curr, modified);
            sender.sendMessage(new TextComponentString(msg));
            LogDB.INSTANCE.log(sender, "charsheet", msg);
        } else {
            sender.sendMessage(new TextComponentString(String.format("%s's %s: %d", player.getDisplayNameString(), res, sheet.resources().get(res))));
        }
    }
}
