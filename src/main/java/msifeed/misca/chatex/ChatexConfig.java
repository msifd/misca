package msifeed.misca.chatex;

import net.minecraft.util.math.MathHelper;

public class ChatexConfig {
    public String wikiUrlBase = "https://wiki.ariadna.su/w/";
    public int offtopRange = 15;
    public int rollRange = 15;

    public int[] speechRanges = {2, 5, 15, 30, 60};

    public int getSpeechRange(int level) {
        final int mid = (speechRanges.length - 1) / 2;
        final int index = MathHelper.clamp(mid + level, 0, speechRanges.length - 1);
        return speechRanges[index];
    }

    public GarbleSettings garble = new GarbleSettings();

    public static class GarbleSettings {
        public double thresholdPart = 0.6;
        public double gray = 0.5;
        public double darkGray = 0.7;
        public double miss = 0.9;
    }
}
