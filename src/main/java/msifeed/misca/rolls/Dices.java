package msifeed.misca.rolls;

import java.util.Random;

public class Dices {
    private static final Random rand = new Random();

    public static boolean check(double chance) {
        return Math.random() < chance;
    }

    public static boolean checkWithNonce(long nonce, double chance) {
        final Random rand = new Random(nonce);
        rand.setSeed(rand.nextLong()); // Nonce itself may be not random enough to produce random numbers
        return rand.nextDouble() < chance;
    }

    public static int checkInt(double chance) {
        return Math.random() < chance ? 1 : 0;
    }

    public static int roll(int min, int d) {
        return min + rand.nextInt(d);
    }
}
