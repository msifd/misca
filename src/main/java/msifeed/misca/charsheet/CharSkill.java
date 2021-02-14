package msifeed.misca.charsheet;

import msifeed.misca.charsheet.cap.CharsheetProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public enum CharSkill {
    psycho, manage, steal, survive, work, biology, tech, magic, invent, firearms, blades,
//    strength, perception, agility, phis_res, psycho_res, planar_knwl, forbidden_knwl,
    ;

//    public boolean isNarrative() {
//        return ordinal() >= strength.ordinal();
//    }

    public int value(EntityLivingBase target) {
        int value = CharsheetProvider.get(target).skills().get(this);
        if (target instanceof EntityPlayer)
            value += (int) target.getEntityAttribute(ICharsheet.SKILL_MOD).getAttributeValue();
        return MathHelper.clamp(value, 0, 5);
    }
}
