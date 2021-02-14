package msifeed.misca.charstate;

import msifeed.misca.MiscaPerms;
import msifeed.misca.charstate.handler.CorruptionHandler;
import msifeed.misca.charstate.handler.IntegrityHandler;
import msifeed.misca.charstate.handler.SanityHandler;
import msifeed.misca.charstate.handler.StaminaHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class NeedsCommand extends CommandBase {
    @Override
    public String getName() {
        return "needs";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/needs <who> <int san sta cor> [<add set> <value>]";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return MiscaPerms.userLevel(sender, "misca.needs");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            case 2:
                return getListOfStringsMatchingLastWord(args,"int", "san", "sta", "cor");
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
        final IAttribute attr = getAttribute(args[1]);
        final IAttributeInstance inst = player.getEntityAttribute(attr);

        if (args.length >= 4 && MiscaPerms.isGameMaster(sender)) {
            final boolean set = args[2].equalsIgnoreCase("set");
            final double value = (float) parseDouble(args[3], -200, 200);
            if (set) inst.setBaseValue(attr.clampValue(value));
            else inst.setBaseValue(attr.clampValue(inst.getBaseValue() + value));
        }

        final String rep = String.format("%s's %s: %.3f (base %.3f)", player.getDisplayNameString(), attr.getName(), inst.getAttributeValue(), inst.getBaseValue());
        sender.sendMessage(new TextComponentString(rep));
    }

    private static IAttribute getAttribute(String name) throws CommandException {
        switch (name) {
            case "int":
            case "integrity":
                return IntegrityHandler.INTEGRITY;
            case "san":
            case "sanity":
                return SanityHandler.SANITY;
            case "sta":
            case "stamina":
                return StaminaHandler.STAMINA;
            case "cor":
            case "corruption":
                return CorruptionHandler.CORRUPTION;
            default:
                throw new SyntaxErrorException("Invalid attribute name");
        }
    }
}
