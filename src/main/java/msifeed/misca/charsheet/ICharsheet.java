package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.sys.cap.IntContainer;
import net.minecraft.util.ResourceLocation;

public interface ICharsheet extends Cloneable {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "sheet");

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