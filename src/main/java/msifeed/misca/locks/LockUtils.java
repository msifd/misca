package msifeed.misca.locks;

import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class LockUtils {
    public static int generateSecret(int pins, int positions) {
        pins = MathHelper.clamp(pins, 1, 8);
        positions = MathHelper.clamp(positions, 1, 15);

        final Random random = new Random();

        int secret = 0;
        for (int i = 0; i < pins * 4; i += 4) {
            final int v = random.nextInt(positions) + 1;
            secret |= v << i;
        }
        return secret;
    }

    public static int getNumberOfPins(int secret) {
        return 8 - Integer.numberOfLeadingZeros(secret) / 4;
    }

    public static String toHex(int secret) {
        return String.format("%08x", secret);
    }

    public static int fromHex(String hex) {
        return Integer.parseUnsignedInt(hex, 16);
    }

    public static void main(String[] args) {
        for (int i = 1; i < 9; i++) {
            int s = generateSecret(i, 9);
            System.out.println(toHex(s) + " " + getNumberOfPins(s));
        }
    }
}