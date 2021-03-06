package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.misca.combat.rules.CombatantInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public enum CharAttribute {
    str, per, end, ref, agi, lck;

    public IAttribute attribute = new RangedAttribute(
            null, Misca.MODID + ".attr." + name(),
            0, 0, 25);

    public double get(EntityLivingBase entity) {
        return entity.getEntityAttribute(ICharsheet.ATTRIBUTE_MOD).getAttributeValue() + entity.getEntityAttribute(attribute).getAttributeValue();
    }

    public double get(CombatantInfo ci) {
        return ci.attributes.get(this);
    }
}
