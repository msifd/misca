package msifeed.misca.locks;

import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class LockUtils {
    public static int generateSecret(int pins, int positions) {
        pins = MathHelper.clamp(pins, 1, 8);
        positions = MathHelper.clamp(positions, 1, 15);

        final Random random = new Random();

        int secret = 0;
        for (int i = 0; i < pins; i++) {
            final int v = random.nextInt(positions) + 1;
            secret |= v << i * 4;
        }
        return secret;
    }

    public static int getNumberOfPins(int secret) {
        return 8 - Integer.numberOfLeadingZeros(secret) / 4;
    }

    public static int getPinPos(int secret, int pos) {
        if (pos < 0 || pos > 7) return 0;
        return secret >> pos * 4 & 0x0f;
    }

    public static int zeroPinPos(int secret, int pos) {
        pos = MathHelper.clamp(pos, 0, 7);
        return secret & ~(0x0f << (pos * 4));
    }

    public static int getNumberOfFreePins(int secret) {
        int left = 0;
        for (int i = 0; i < 8; i++) {
            if (getPinPos(secret, i) != 0)
                left++;
        }
        return left;
    }

    public static int getFirstFreePin(int secret, int skip) {
        for (int i = skip; i < 8 + skip; i++) {
            if (getPinPos(secret, i % 8) != 0)
                return i % 8;
        }
        return -1;
    }

    public static int getMaxPin(int secret) {
        int max = 0;
        for (int i = 0; i < 8; i++) {
            if (getPinPos(secret, i % 8) != 0)
                return i % 8;
        }
        return -1;
    }

    public static String toHex(int secret) {
        return String.format("%08x", secret);
    }

    public static int fromHex(String hex) {
        return Integer.parseUnsignedInt(hex, 16);
    }

    public static void main(String[] args) {
        int s = generateSecret(5, 9);
        System.out.println(toHex(s) + " " + getNumberOfPins(s));

        for (int i = 0; i < 8; i++) {
            s = zeroPinPos(s, i);
            System.out.println(toHex(s) + " " + getNumberOfFreePins(s) + " " + i);
        }
    }
}