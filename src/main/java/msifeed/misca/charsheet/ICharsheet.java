package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.sys.cap.IntContainer;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.ResourceLocation;

public interface ICharsheet extends Cloneable {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "sheet");
    IAttribute ATTRIBUTE_MOD = new RangedAttribute(null, Misca.MODID + ".attrMod", 0, -100, 100);
    IAttribute SKILL_MOD = new RangedAttribute(null, Misca.MODID + ".skillMod", 0, -5, 5);

    String getName();
    void setName(String name);

    String getWikiPage();
    void setWikiPage(String page);

    IntContainer<CharSkill> skills();
    IntContainer<CharEffort> effortPools();
    IntContainer<CharResource> resources();

    void replaceWith(ICharsheet charsheet);
    ICharsheet clone();
}