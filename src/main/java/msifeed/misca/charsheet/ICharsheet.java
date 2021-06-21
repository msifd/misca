package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.sys.cap.FloatContainer;
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
    FloatContainer<CharNeed> needsGain();
    FloatContainer<CharNeed> needsLost();

    Map<Potion, Integer> potions();
    Map<Enchantment, Integer> enchants();

    long getLastUpdated();
    void setLastUpdated(long value);
    default long timeSinceUpdate() {
        return System.currentTimeMillis() / 1000 - getLastUpdated();
    }

    void replaceWith(ICharsheet charsheet);
    ICharsheet clone();
}