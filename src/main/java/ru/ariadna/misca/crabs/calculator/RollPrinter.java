package ru.ariadna.misca.crabs.calculator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import ru.ariadna.misca.crabs.characters.CharStats;
import ru.ariadna.misca.database.DBHandler;

public class RollPrinter {
    public static void sendActionRollResult(EntityPlayer player, CalcResult result) {
        String calc_msg;
        if (result.mod != 0)
            calc_msg = String.format("%s [%s]%+d%+d=%d", result.action.toPrettyString(), makeCritical20(result.dice), result.stats, result.mod, result.result);
        else
            calc_msg = String.format("%s [%s]%+d=%d", result.action.toPrettyString(), makeCritical20(result.dice), result.stats, result.result);

        String msg = String.format("\u00A76[Roll] %s: %s", player.getDisplayName(), calc_msg);
        send(msg, player);
        DBHandler.INSTANCE.logMessage(player, "dice", calc_msg);
    }

    public static void sendCustomRollResult(EntityPlayer player, CharStats charStat, int dice, int stat, int mod) {
        String calc_msg;
        if (mod != 0)
            calc_msg = String.format("%s [%s]%+d%+d=%d", charStat.pretty(), makeCritical10(dice), stat, mod, dice + stat + mod);
        else
            calc_msg = String.format("%s [%s]%+d=%d", charStat.pretty(), makeCritical10(dice), stat, dice + stat);

        String msg = String.format("\u00A76[Roll Stat] %s: %s", player.getDisplayName(), calc_msg);
        send(msg, player);
        DBHandler.INSTANCE.logMessage(player, "dice", calc_msg);
    }

    private static String makeCritical20(int dice) {
        if (dice <= 3) return "\u00A74" + dice + "\u00A76";
        else if (dice >= 18) return "\u00A72" + dice + "\u00A76";
        else return String.valueOf(dice);
    }

    private static String makeCritical10(int dice) {
        if (dice == 1) return "\u00A74" + dice + "\u00A76";
        else if (dice == 10) return "\u00A72" + dice + "\u00A76";
        else return String.valueOf(dice);
    }

    private static void send(String msg, EntityPlayer center) {
        center.getEntityWorld().playerEntities.stream()
                .filter(p -> ((EntityPlayer) p).getDistanceToEntity(center) <= 15)
                .forEach(o -> ((EntityPlayerMP) o).addChatMessage(new ChatComponentText(msg)));
    }
}
