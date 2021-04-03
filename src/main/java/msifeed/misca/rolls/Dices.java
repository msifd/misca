package msifeed.misca.rolls;

import java.util.Random;

public class Dices {
    private static final Random rand = new Random();

    public static boolean check(double chance) {
        return rand.nextFloat() < chance;
    }

    public static int checkInt(double chance) {
        return rand.nextDouble() < chance ? 1 : 0;
    }

    public static int roll(int min, int d) {
        return min + rand.nextInt(d);
    }
}
