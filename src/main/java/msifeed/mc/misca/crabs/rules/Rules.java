package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.crabs.battle.BattleDefines;
import msifeed.mc.misca.crabs.battle.MoveFormatter;
import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.CharacterManager;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.HashMap;
import java.util.stream.Stream;

public final class Rules {
    private static final HashMap<String, Modifier> modifiers = new HashMap<>();
    private static final HashMap<String, Effect> effects = new HashMap<>();

    static {
        Stream.of(
                new Modifier.DiceG30(),
                new Modifier.DiceG30Plus(),
                new Modifier.DiceG30Minus()
        ).forEach(m -> modifiers.put(m.toString(), m));
        Stream.of(Stats.values())
                .map(Modifier.Stat::new)
                .forEach(m -> modifiers.put(m.toString().toLowerCase(), m));

        Stream.of(
                new Effect.Damage(),
                new Effect.Fire()
        ).forEach(e -> effects.put(e.toString(), e));
    }

    public static Modifier mod(String s) {
        if (modifiers.containsKey(s)) return modifiers.get(s);
        try {
            return new Modifier.Const(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Effect effect(String s) {
        return effects.get(s);
    }

    public static ActionResult computeWinner(ActionResult a, ActionResult b) {
        while (a.compareTo(b) == 0) {
            a.throwDices(a.character);
            b.throwDices(b.character);
        }
        return a.compareTo(b) > 0 ? a : b;
    }

    public static void rollSingleStat(EntityPlayerMP player, Stats stat, int mod) {
        final Character c = CharacterManager.INSTANCE.get(player.getUniqueID());
        if (c == null) return;

        final int roll = DiceMath.g30();
        final String msg = MoveFormatter.formatStatRoll(c, stat, roll, mod);
        MiscaUtils.notifyAround(
                player, BattleDefines.NOTIFICATION_RADIUS,
                new ChatComponentText(msg)
        );
    }
}
