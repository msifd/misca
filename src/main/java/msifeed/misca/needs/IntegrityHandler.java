package msifeed.misca.needs;

import msifeed.misca.Misca;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class IntegrityHandler {
    public static final IAttribute INTEGRITY = new RangedAttribute(null, Misca.MODID + ".integrity", 100, 0, 100).setShouldWatch(true);

    private final Potion slowness = Potion.getPotionById(2);
    private final Potion miningFatigue = Potion.getPotionById(4);
    private final Potion weakness = Potion.getPotionById(18);

    public void handleTime(EntityPlayer player, IAttributeInstance attr, long secs) {
        final double intPerSec = 3d / (5 * 60 * 60);
//        final double intPerSec = 3d / (5);
        final double restored = secs * intPerSec;
        final double value = attr.getAttribute().clampValue(attr.getBaseValue() + restored);

        attr.setBaseValue(value);
//        System.out.printf("int restored: %.5f, base: %.5f\n", restored, attr.getBaseValue());

        if (value <= 75) addPotionEffect(player, miningFatigue, value <= 50 ? 1 : 0);
        if (value <= 50) addPotionEffect(player, weakness, value <= 25 ? 1 : 0);
        if (value <= 25) addPotionEffect(player, slowness, 0);
    }

    private static void addPotionEffect(EntityPlayer player, Potion potion, int amplification) {
        final PotionEffect effect = new PotionEffect(potion, 120, amplification, true, false);
        player.addPotionEffect(effect);
    }

    public void handleDamage(EntityPlayer player, IAttributeInstance attr, float amount) {
        final double lost = amount * 0.1;
        attr.setBaseValue(attr.getAttribute().clampValue(attr.getBaseValue() - lost));
    }
}
