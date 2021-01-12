package msifeed.misca.needs;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.ICharsheet;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class SanityHandler {
    public static final IAttribute SANITY = new RangedAttribute(null, Misca.MODID + ".sanity", 100, 0, 150).setShouldWatch(true);

    private static final AttributeModifier ATTRIBUTE_PENALTY_1 = new AttributeModifier(UUID.fromString("15bf9288-0473-409f-86cf-499a729e3b46"), "Attribute penalty I", -2, 0).setSaved(false);
    private static final AttributeModifier ATTRIBUTE_PENALTY_2 = new AttributeModifier(UUID.fromString("436866e3-3052-4d93-a54f-bfd7d593f532"), "Attribute penalty II", -4, 0).setSaved(false);
    private static final AttributeModifier ATTRIBUTE_PENALTY_3 = new AttributeModifier(UUID.fromString("a7e0d58e-633a-495e-a68c-b54b6a9134b7"), "Attribute penalty III", -8, 0).setSaved(false);

    private static final AttributeModifier SKILL_PENALTY_1 = new AttributeModifier(UUID.fromString("45d90c52-6ed7-4bb2-8ad1-7d8ba23f84c5"), "Attribute penalty I", -1, 0).setSaved(false);
    private static final AttributeModifier SKILL_PENALTY_2 = new AttributeModifier(UUID.fromString("49495e01-77e9-4c1a-8b5c-bab0935bd898"), "Attribute penalty II", -2, 0).setSaved(false);

    public void handle(EntityPlayer player, AbstractAttributeMap attributes, long secs) {
        final IAttributeInstance attr = attributes.getAttributeInstance(SanityHandler.SANITY);

        // TODO: disable darkness
        final int light = player.world.getLight(player.getPosition(), false);
        final double darkness = light < 7 ? (1d / (30 * 60)) : 0;
        final double sanPerSec = 1d / (60 * 60) + darkness;
        final double lost = secs * sanPerSec;
        final double value = attr.getAttribute().clampValue(attr.getBaseValue() - lost);

        attr.setBaseValue(value);
//        System.out.printf("san lost: %.5f, base: %.5f\n", lost, attr.getBaseValue());

        final IAttributeInstance attributeMod = attributes.getAttributeInstance(ICharsheet.ATTRIBUTE_MOD);
        setMod(attributeMod, ATTRIBUTE_PENALTY_1, value <= 75 && value > 50);
        setMod(attributeMod, ATTRIBUTE_PENALTY_2, value <= 50 && value > 25);
        setMod(attributeMod, ATTRIBUTE_PENALTY_3, value <= 25);

        final IAttributeInstance skillMod = attributes.getAttributeInstance(ICharsheet.SKILL_MOD);
        setMod(skillMod, SKILL_PENALTY_1, value <= 50 && value > 25);
        setMod(skillMod, SKILL_PENALTY_2, value <= 25);
    }

    private static void setMod(IAttributeInstance attr, AttributeModifier mod, boolean active) {
        if (active) attr.applyModifier(mod);
        else if (attr.hasModifier(mod)) attr.removeModifier(mod);
    }

    public void handleDamage(EntityPlayer player, IAttributeInstance attr, float amount) {
        final double lost = amount * 0.1;
        attr.setBaseValue(attr.getAttribute().clampValue(attr.getBaseValue() - lost));
    }
}
