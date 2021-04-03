package msifeed.misca.combat;

import msifeed.misca.Misca;
import msifeed.misca.combat.rules.CombatantInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public enum CharAttribute {
    str, per, end, ref, agi, lck;

    public static final IAttribute MOD = new RangedAttribute(null, Misca.MODID + ".attrMod", 0, -100, 100);

    public IAttribute attribute = new RangedAttribute(
            null, Misca.MODID + ".attr." + name(),
            0, 0, 25).setShouldWatch(true);

    public int getBase(EntityLivingBase entity) {
        return (int) entity.getEntityAttribute(attribute).getBaseValue();
    }


    public double get(EntityLivingBase entity) {
        return entity.getEntityAttribute(MOD).getAttributeValue() + entity.getEntityAttribute(attribute).getAttributeValue();
    }

    public double get(CombatantInfo ci) {
        return ci.attributes.get(this);
    }
}
