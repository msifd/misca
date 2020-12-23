package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import net.minecraft.util.ResourceLocation;

public interface ICharsheet extends Cloneable {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "char");

    boolean isPlayer();
    void markPlayer();

    String getName();
    void setName(String name);

    String getWikiPage();
    void setWikiPage(String page);

    ValueContainer<CharAttribute> attrs();
    ValueContainer<CharResource> resources();

    void replaceWith(ICharsheet charsheet);
    ICharsheet clone();
}