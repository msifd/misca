package msifeed.misca.charsheet.cap;

import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charsheet.CharResource;
import msifeed.misca.charsheet.CharSkill;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.sys.cap.IntContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;

import java.util.HashMap;
import java.util.Map;

public class CharsheetImpl implements ICharsheet {
    private String name = "";
    private String wikiPage = "";
    private final IntContainer<CharSkill> skills = new IntContainer<>(CharSkill.class, 0, 25);
    private final IntContainer<CharEffort> effortPools = new IntContainer<>(CharEffort.class, 0, 50);
    private final IntContainer<CharResource> resources = new IntContainer<>(CharResource.class, 0, 1000);
    private final HashMap<Potion, Integer> potions = new HashMap<>();
    private final HashMap<Enchantment, Integer> enchants = new HashMap<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        if (name.length() > MAX_NAME_LENGTH)
            this.name = name.substring(0, MAX_NAME_LENGTH);
        else
            this.name = name;
    }

    @Override
    public String getWikiPage() {
        return wikiPage;
    }

    @Override
    public void setWikiPage(String page) {
        this.wikiPage = page;
    }

    @Override
    public IntContainer<CharSkill> skills() {
        return skills;
    }

    @Override
    public IntContainer<CharEffort> effortPools() {
        return effortPools;
    }

    @Override
    public IntContainer<CharResource> resources() {
        return resources;
    }

    @Override
    public Map<Potion, Integer> potions() {
        return potions;
    }

    @Override
    public Map<Enchantment, Integer> enchants() {
        return enchants;
    }

    @Override
    public void replaceWith(ICharsheet charsheet) {
        name = charsheet.getName();
        wikiPage = charsheet.getWikiPage();
        skills.replaceWith(charsheet.skills());
        effortPools.replaceWith(charsheet.effortPools());
        resources.replaceWith(charsheet.resources());
        potions.clear();
        potions.putAll(charsheet.potions());
        enchants.clear();
        enchants.putAll(charsheet.enchants());
    }

    @Override
    public ICharsheet clone() {
        final CharsheetImpl clone;
        try {
            clone = (CharsheetImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        return clone;
    }
}
