package msifeed.mc.misca.crabs.calc;

import java.util.Random;

public final class DiceMath {
    private static final Random rand = new Random();

    public static double gauss(double mean, int min, int max) {
        final double std_dev = min / 2. + max / 2.;
        double roll = min - 1;
        while (roll < min || roll > max) {
            roll = rand.nextGaussian() * mean + std_dev;
        }
        return roll;
    }

    public static int g30() {
        return (int) Math.floor(gauss(6.8, 1, 31));
    }

    public static int g30_plus() {
        return (int) Math.floor(gauss(6.8, 3, 31));
    }

    public static int g30_minus() {
        return (int) Math.floor(gauss(6.8, 1, 29));
    }
}
