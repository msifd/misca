package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.sys.cap.IntContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public interface ICharsheet extends Cloneable {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "sheet");
    int MAX_NAME_LENGTH = 30;

    String getName();
    void setName(String name);

    String getWikiPage();
    void setWikiPage(String page);

    IntContainer<CharSkill> skills();
    IntContainer<CharEffort> effortPools();
    IntContainer<CharResource> resources();

    Map<Potion, Integer> potions();
    Map<Enchantment, Integer> enchants();

    void replaceWith(ICharsheet charsheet);
    ICharsheet clone();
}