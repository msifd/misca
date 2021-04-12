package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public enum CharSkill {
    psychology,
    management,
    thievery,
    survival,
    hardworking,
    biology,
    engineering,
    sorcery,
    research,
    blacksmith,
    magic;

    public static final IAttribute MOD = new RangedAttribute(null, Misca.MODID + ".skillMod", 0, -5, 5);

    public int get(EntityPlayer target) {
        final int value = CharsheetProvider.get(target).skills().get(this) + (int) target.getEntityAttribute(MOD).getAttributeValue();
        return MathHelper.clamp(value, 0, 5);
    }

    public String tr() {
        return I18n.format("enum.misca.skill." + name());
    }
}
