package msifeed.misca.cmd;

import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.CharsheetSync;
import net.minecraft.command.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlessCommand extends CommandBase  {
    private static final String potionArg = "potion";
    private static final String enchantArg = "enchant";

    @Override
    public String getName() {
        return "bless";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/bless <who> <potion / enchant / list> <name> <level or 0 to del>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return MiscaPerms.userLevel(sender, "misca.bless");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            case 2:
                return getListOfStringsMatchingLastWord(args, potionArg, enchantArg, "list", "clear");
            case 3:
                switch (args[1].toLowerCase()) {
                    case potionArg:
                        return getListOfStringsMatchingLastWord(args, ForgeRegistries.POTIONS.getKeys());
                    case enchantArg:
                        return getListOfStringsMatchingLastWord(args, Enchantment.REGISTRY.getKeys());
                    default:
                        return Collections.emptyList();
                }
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) throw new SyntaxErrorException("Usage: " + getUsage(sender));

        final EntityPlayerMP player = getPlayer(server, sender, args[0]);

        switch (args[1].toLowerCase()) {
            case potionArg:
                if (args.length < 4) throw new SyntaxErrorException("Usage: " + getUsage(sender));
                addPotion(sender, player, args[2], parseLevel(args[3]));
                break;
            case enchantArg:
                if (args.length < 4) throw new SyntaxErrorException("Usage: " + getUsage(sender));
                addEnchant(sender, player, args[2], parseLevel(args[3]));
                break;
            case "list":
                listBlessings(sender, player);
                break;
            case "clear":
                clearBlessings(sender, player);
                break;
            default:
                throw new SyntaxErrorException("Usage: " + getUsage(sender));
        }
    }

    private void addPotion(ICommandSender sender, EntityPlayerMP player, String key, int level) throws SyntaxErrorException {
        final Potion potion = Potion.getPotionFromResourceLocation(key);
        if (potion == null) throw new SyntaxErrorException("Unknown potion: " + key);

        final ICharsheet sheet = CharsheetProvider.get(player);
        if (level > 0) {
            sheet.potions().put(potion, level - 1);
            sender.sendMessage(new TextComponentString("Potion blessing added"));
        } else {
            sheet.potions().remove(potion);
            sender.sendMessage(new TextComponentString("Potion blessing removed"));
        }

        CharsheetSync.sync(player);
    }

    private void addEnchant(ICommandSender sender, EntityPlayerMP player, String key, int level) throws SyntaxErrorException {
        final Enchantment enchantment = Enchantment.getEnchantmentByLocation(key);
        if (enchantment == null) throw new SyntaxErrorException("Unknown enchant: " + key);

        final ICharsheet sheet = CharsheetProvider.get(player);
        if (level > 0) {
            sheet.enchants().put(enchantment, level);
            sender.sendMessage(new TextComponentString("Enchant blessing added"));
        } else {
            sheet.enchants().remove(enchantment);
            sender.sendMessage(new TextComponentString("Enchant blessing removed"));
        }

        CharsheetSync.sync(player);
    }

    private void listBlessings(ICommandSender sender, EntityPlayerMP player) {
        final ICharsheet sheet = CharsheetProvider.get(player);

        final ArrayList<String> blessings = new ArrayList<>();
        sheet.potions().forEach((p, level) -> blessings.add(p.getRegistryName().toString() + "@" + (level + 1)));
        sheet.enchants().forEach((e, level) -> blessings.add(e.getRegistryName().toString() + "@" + level));

        sender.sendMessage(new TextComponentString("Blessings: " + joinNiceStringFromCollection(blessings)));
    }

    private void clearBlessings(ICommandSender sender, EntityPlayerMP player) {
        final ICharsheet sheet = CharsheetProvider.get(player);
        sheet.potions().clear();
        sheet.enchants().clear();

        CharsheetSync.sync(player);

        sender.sendMessage(new TextComponentString("Blessings cleared"));
    }

    private int parseLevel(String str) throws NumberInvalidException {
        return Math.min(parseInt(str), Byte.MAX_VALUE);
    }
}
