package msifeed.misca.charstate.potion;

import net.minecraft.potion.Potion;

public class TemporaryNeedsPotion extends Potion {
    public TemporaryNeedsPotion(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    public boolean isReady(int duration, int amplifier) {
        final int k = 50 >> amplifier;
        return k > 0 && duration % k == 0;
    }
}
