package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.crabs.battle.ActionFormatter;
import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.CharacterManager;
import msifeed.mc.misca.crabs.character.Stats;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public final class Rules {
    public static ActionResult computeWinner(ActionResult a, ActionResult b) {
        do {
            a.throwDices(a.character);
            b.throwDices(b.character);
        } while (a.compareTo(b) == 0);
        return a.compareTo(b) > 0 ? a : b;
    }

    public static void rollSingleStat(EntityPlayerMP player, Stats stat, int mod) {
        final Character c = CharacterManager.INSTANCE.get(player.getUniqueID());
        if (c == null) return;

        final int roll = DiceMath.g30();

        String msg = ActionFormatter.formatStatRoll(stat, roll, c.stat(stat), mod);
        player.addChatMessage(new ChatComponentText(msg));
    }
}
