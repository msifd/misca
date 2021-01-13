package msifeed.misca.needs;

import msifeed.misca.Misca;
import msifeed.misca.needs.cap.IPlayerNeeds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class IntegrityHandler {
    private final Potion slowness = Potion.getPotionById(2);
    private final Potion miningFatigue = Potion.getPotionById(4);
    private final Potion weakness = Potion.getPotionById(18);

    public void handleTime(EntityPlayer player, IPlayerNeeds needs, long secs) {
        final NeedsConfig config = Misca.getSharedConfig().needs;
        final double restored = secs * config.integrityRestPerSec;
        needs.add(IPlayerNeeds.NeedType.integrity, restored);

        final double value = needs.get(IPlayerNeeds.NeedType.integrity);

        if (value <= 75) addPotionEffect(player, miningFatigue, value <= 50 ? 1 : 0);
        if (value <= 50) addPotionEffect(player, weakness, value <= 25 ? 1 : 0);
        if (value <= 25) addPotionEffect(player, slowness, 0);
    }

    private static void addPotionEffect(EntityPlayer player, Potion potion, int amplification) {
        final PotionEffect effect = new PotionEffect(potion, 120, amplification, true, false);
        player.addPotionEffect(effect);
    }

    public void handleDamage(IPlayerNeeds needs, float amount) {
        final NeedsConfig config = Misca.getSharedConfig().needs;
        final double lost = amount * config.integrityCostPerDamage;
        needs.add(IPlayerNeeds.NeedType.integrity, -lost);
    }
}
