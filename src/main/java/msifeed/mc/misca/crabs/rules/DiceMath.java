package msifeed.mc.misca.crabs.rules;

import java.util.Random;

public final class DiceMath {
    private static final Random rand = new Random();

    public static double gauss(double mean, int from, int to) {
        final double std_dev = from / 2. + to / 2.;
        double roll = from - 1;
        while (roll < from || roll > to) {
            roll = rand.nextGaussian() * mean + std_dev;
        }
        return roll;
    }

    public static int g15() {
        return (int) Math.floor(gauss(4.15, 1, 16));
    }

    public static int g40() {
        return (int) Math.floor(gauss(9.01, 1, 41));
    }

    public static int g40_plus() {
        return (int) Math.floor(gauss(9.01, 3, 41));
    }

    public static int g40_minus() {
        return (int) Math.floor(gauss(9.01, 1, 39));
    }

    public enum DiceRank {
        REGULAR, FAIL, LUCK;

        public static DiceRank ofD15(int roll) {
            if (roll == 15) return LUCK;
            else if (roll == 1) return FAIL;
            else return REGULAR;
        }

        public static DiceRank ofD40(int roll) {
            if (roll >= 37) return LUCK;
            else if (roll <= 4) return FAIL;
            else return REGULAR;
        }

        public boolean beats(DiceRank other) {
            return (this == LUCK && other != LUCK) || (other == FAIL && this != FAIL);
        }
    }
}
