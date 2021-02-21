package msifeed.misca.charstate.handler;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharSkill;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charstate.CharstateConfig;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class IntegrityHandler {
    public static final IAttribute INTEGRITY = new RangedAttribute(null, Misca.MODID + ".integrity", 100, 0, 100);

    private final Potion slowness = Potion.getPotionById(2);
    private final Potion miningFatigue = Potion.getPotionById(4);
    private final Potion weakness = Potion.getPotionById(18);

    public void handleTime(EntityPlayer player, long secs) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final double restored = secs * config.integrityRestPerSec;

        final IAttributeInstance inst = player.getEntityAttribute(INTEGRITY);
        inst.setBaseValue(INTEGRITY.clampValue(inst.getBaseValue() + restored));

        final double value = inst.getAttributeValue();
        if (value <= 75) addPotionEffect(player, miningFatigue, value <= 50 ? 1 : 0);
        if (value <= 50) addPotionEffect(player, weakness, value <= 25 ? 1 : 0);
        if (value <= 25) addPotionEffect(player, slowness, 0);
    }

    private static void addPotionEffect(EntityPlayer player, Potion potion, int amplification) {
        final PotionEffect effect = new PotionEffect(potion, 120, amplification, true, false);
        player.addPotionEffect(effect);
    }

    public void handleDamage(EntityPlayer player, float amount) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final double factor = 1 + CharsheetProvider.get(player).skills().get(CharSkill.survival) * config.survivalSkillNeedsLostFactor;
        final double lost = amount * config.integrityCostPerDamage * factor;

        final IAttributeInstance inst = player.getEntityAttribute(INTEGRITY);
        inst.setBaseValue(INTEGRITY.clampValue(inst.getBaseValue() - lost));
    }
}