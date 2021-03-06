package msifeed.misca.charsheet;

import msifeed.misca.charsheet.cap.CharsheetProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public enum CharSkill {
    psychology, management, biology, tech, magic, research,
    thievery, survival, work, firearms, blacksmith;

    public int get(EntityPlayer target) {
        final int value = CharsheetProvider.get(target).skills().get(this) + (int) target.getEntityAttribute(ICharsheet.SKILL_MOD).getAttributeValue();
        return MathHelper.clamp(value, 0, 5);
    }
}
