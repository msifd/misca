package msifeed.misca.combat.rules;

import java.util.Random;

public class Dices {
    private static final Random rand = new Random();

    public static boolean check(float chance) {
        return rand.nextFloat() < chance;
    }

    public static float checkFloat(float chance) {
        return rand.nextFloat() < chance ? 1 : 0;
    }

    public static int d20() {
        return rand.nextInt(20) + 1;
    }
}
