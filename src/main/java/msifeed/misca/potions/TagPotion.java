package msifeed.misca.potions;

import net.minecraft.potion.Potion;

public class TagPotion extends Potion {
    protected TagPotion(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }
}
