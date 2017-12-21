package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.crabs.battle.BattleDefines;
import msifeed.mc.misca.crabs.battle.FighterContext;
import msifeed.mc.misca.crabs.battle.MoveFormatter;
import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.CharacterManager;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

public final class Rules {
    private static final Logger logger = LogManager.getLogger("Crabs.Rules");

    private static final HashMap<String, Modifier> modifiers = new HashMap<>();
    private static final HashMap<String, Class<? extends Effect>> effects = new HashMap<>();
    private static final HashMap<String, Effect> basicEffects = new HashMap<>();

    static {
        Stream.of(
                new Modifier.DiceG30(),
                new Modifier.DiceG30Plus(),
                new Modifier.DiceG30Minus()
        ).forEach(m -> modifiers.put(m.name(), m));
        Stream.of(Stats.values())
                .map(Modifier.Stat::new)
                .forEach(m -> modifiers.put(m.name().toLowerCase(), m));

        Stream.of(
                new Effect.Damage(),
                new Effect.Fire()
        ).forEach(e -> {
            basicEffects.put(e.name(), e);
            effects.put(e.name(), e.getClass());
        });

        effects.put("buff", Buff.class);
        effects.put("score", DynamicEffect.Score.class);
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
        final String trimmed = s.trim();
        final int sep1Index = trimmed.indexOf(':');
        final boolean hasArgs = sep1Index >= 0 && trimmed.length() - sep1Index > 1;
        final String name = sep1Index < 0 ? trimmed : trimmed.substring(0, sep1Index);

        final Class<? extends Effect> effectClass = effects.get(name);
        if (effectClass == null) return null;

        try {
            final boolean dynamic = DynamicEffect.class.isAssignableFrom(effectClass);
            if (dynamic) {
                if (!hasArgs) throw new ParseException("DynEffect `" + name + "` requires args!", trimmed.length() - 1);
                final String rest = trimmed.substring(sep1Index + 1);
                return parseDynEffect(effectClass, rest);
            } else {
                return basicEffects.get(name);
            }
        } catch (Exception e) {
            logger.error("Failed to parse buff effect: {}: {}. Effect source: `{}`", e.getClass().getSimpleName(), e.getMessage(), trimmed);
        }

        return null;
    }

    private static Effect parseDynEffect(Class<? extends Effect> effectClass, String rest) throws Exception {
        final Constructor constructor = effectClass.getConstructor();
        final DynamicEffect effect = (DynamicEffect) constructor.newInstance();
        final String[] argStrs = rest.split(":");

        final DynamicEffect.EffectArgs[] argTypes = effect.args();
        if (argStrs.length < argTypes.length)
            throw new ParseException(String.format("Too few args! Specified %d, expected: %d", argStrs.length, argTypes.length), 0);

        int restOffset = 0;
        final Object[] args = new Object[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            final String argStr = argStrs[i];
            switch (argTypes[i]) {
                case INT:
                    args[i] = Integer.parseInt(argStr);
                    restOffset += argStr.length() + 1; // +1 for separator
                    break;
                case EFFECT:
                    if (argStr.equals("buff")) throw new ParseException("Nested buffs are forbidden!", restOffset);
                    final Effect e = effect(rest.substring(restOffset));
                    args[i] = e;
                    i += 1;
                    if (e instanceof DynamicEffect)
                        i += ((DynamicEffect) e).args().length;
                    // TODO add sub-buff support? hehe
                    // TODO calc offset for skipped
                    break;
            }
        }

        effect.init(args);

        return effect;
    }

    public static ActionResult computeWinner(ActionResult a, ActionResult b) {
        do {
            a.throwDices(a.character);
            b.throwDices(b.character);
        } while (a.compareTo(b) == 0);
        return a.compareTo(b) > 0 ? a : b;
    }

    public static void rollSingleStat(EntityPlayerMP player, FighterContext ctx, Stats stat, int mod) {
        final UUID actor = ctx != null
                ? ctx.control != null ? ctx.control : ctx.uuid
                : player.getUniqueID();

        final Character c = CharacterManager.INSTANCE.getNullable(actor);
        if (c == null) {
            player.addChatMessage(new ChatComponentText("Update stats at first."));
            return;
        }

        final int roll = DiceMath.g30();
        final String msg = MoveFormatter.formatStatRoll(c, stat, roll, mod);
        MiscaUtils.notifyAround(
                player, BattleDefines.NOTIFICATION_RADIUS,
                new ChatComponentText(msg)
        );
    }
}
