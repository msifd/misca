package msifeed.misca.chatex;

import net.minecraft.util.math.MathHelper;

public class ChatexConfig {
    public int[] speechRanges = {2, 5, 15, 30, 60};
    public GarbleSettings garble = new GarbleSettings();
    public String wikiUrlBase = "https://wiki.ariadna.su/w/";
    public int offtopRange = 15;
    public int diceRollRange = 15;

    public int getSpeechRange(int level) {
        final int mid = (speechRanges.length - 1) / 2;
        final int index = MathHelper.clamp(mid + level, 0, speechRanges.length - 1);
        return speechRanges[index];
    }

    public static class GarbleSettings {
        public int thresholdDistance = 4;
        public float grayThreshold = 0.33f;
        public float darkGrayThreshold = 0.66f;
        public float missThreshold = 0.9f;
    }
}
