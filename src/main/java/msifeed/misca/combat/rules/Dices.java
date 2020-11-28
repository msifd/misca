package msifeed.misca.combat.rules;

import java.util.Random;

public class Dices {
    private static final Random rand = new Random();

    public static boolean check(double chance) {
        return rand.nextFloat() < chance;
    }

    public static double checkFloat(double chance) {
        return rand.nextDouble() < chance ? 1 : 0;
    }

    public static int d20() {
        return rand.nextInt(20) + 1;
    }
}
