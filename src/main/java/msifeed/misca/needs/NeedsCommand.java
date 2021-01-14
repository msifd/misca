package msifeed.misca.needs;

import msifeed.misca.needs.handler.CorruptionHandler;
import msifeed.misca.needs.handler.IntegrityHandler;
import msifeed.misca.needs.handler.SanityHandler;
import msifeed.misca.needs.handler.StaminaHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
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
        return "/needs <who> <int san sta cor> [<add set> <value>]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) throw new SyntaxErrorException("Expected at least 2 args");

        final EntityPlayerMP player = getPlayer(server, sender, args[0]);
        final IAttribute attr = getAttribute(args[1]);
        final IAttributeInstance inst = player.getEntityAttribute(attr);

        if (args.length >= 4) {
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
