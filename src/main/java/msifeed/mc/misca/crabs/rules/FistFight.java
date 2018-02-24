package msifeed.mc.misca.crabs.rules;

import java.util.EnumMap;

import msifeed.mc.misca.crabs.character.Stats;
import net.minecraft.client.resources.I18n;

import static msifeed.mc.misca.crabs.character.Stats.*;
import static msifeed.mc.misca.crabs.rules.FistFight.Action.*;

public class FistFight {
    public static final EnumMap<Action, Stats[]> rules = new EnumMap<>(Action.class);

    static {
        rules.put(HOOK, new Stats[]{STR, END});
        rules.put(JEB, new Stats[]{STR, PER});
        rules.put(KICK, new Stats[]{REF, SPR});
        rules.put(MAGIC_PUNCH, new Stats[]{SPR, INT});
        rules.put(BLOCK, new Stats[]{WIL, END});
        rules.put(DODGE, new Stats[]{DET, REF});
        rules.put(PARRY, new Stats[]{PER, INT});
        rules.put(MAGIC_BLOCK, new Stats[]{WIL, DET});
    }

    public enum Action {
        HOOK, JEB, KICK, MAGIC_PUNCH, BLOCK, DODGE, PARRY, MAGIC_BLOCK;

        public String pretty() {
            return I18n.format("misca.crabs.action." + this.toString().toLowerCase());
        }
    }
}
