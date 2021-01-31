package msifeed.misca.charsheet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public enum CharSkill {
    psycho, manage, steal, survive, work, biology, tech, magic, invent, firearms, blades,
    strength, perception, agility, phis_res, psycho_res, planar_knwl, forbidden_knwl;

    public boolean isNarrative() {
        return ordinal() >= strength.ordinal();
    }

    public int value(EntityPlayer player) {
        final int mod = (int) player.getEntityAttribute(ICharsheet.SKILL_MOD).getAttributeValue();
        final int value = CharsheetProvider.get(player).skills().get(this);
        return MathHelper.clamp(value + mod, 0, 5);
    }
}
