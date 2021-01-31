package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.ResourceLocation;

public interface ICharsheet extends Cloneable {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "char");
    IAttribute ATTRIBUTE_MOD = new RangedAttribute(null, Misca.MODID + ".attrMod", 0, -100, 100).setShouldWatch(true);
    IAttribute SKILL_MOD = new RangedAttribute(null, Misca.MODID + ".skillMod", 0, -5, 5).setShouldWatch(true);

    boolean isPlayer();
    void markPlayer();

    String getName();
    void setName(String name);

    String getWikiPage();
    void setWikiPage(String page);

    ValueContainer<CharAttribute> attrs();
    ValueContainer<CharSkill> skills();
    ValueContainer<CharResource> resources();

    void replaceWith(ICharsheet charsheet);
    ICharsheet clone();
}