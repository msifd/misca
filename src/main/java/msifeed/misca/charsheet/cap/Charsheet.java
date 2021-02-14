package msifeed.misca.charsheet.cap;

import msifeed.misca.charsheet.*;
import msifeed.sys.cap.IntContainer;

public class Charsheet implements ICharsheet {
    private boolean isPlayer = false;
    private String name = "";
    private String wikiPage = "";
    private final IntContainer<CharAttribute> attributes = new IntContainer<>(CharAttribute.class, 0, 25);
    private final IntContainer<CharSkill> skills = new IntContainer<>(CharSkill.class, 0, 25);
    private final IntContainer<CharEffort> effortPools = new IntContainer<>(CharEffort.class, 0, 50);
    private final IntContainer<CharResource> resources = new IntContainer<>(CharResource.class, 0, 1000);

    @Override
    public boolean isPlayer() {
        return isPlayer;
    }

    @Override
    public void markPlayer() {
        isPlayer = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
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
    public IntContainer<CharAttribute> attrs() {
        return attributes;
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
    public void replaceWith(ICharsheet charsheet) {
        if (charsheet.isPlayer())
            markPlayer();
        name = charsheet.getName();
        wikiPage = charsheet.getWikiPage();
        attributes.replaceWith(charsheet.attrs());
        skills.replaceWith(charsheet.skills());
        effortPools.replaceWith(charsheet.effortPools());
        resources.replaceWith(charsheet.resources());
    }

    @Override
    public ICharsheet clone() {
        final Charsheet clone;
        try {
            clone = (Charsheet) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        return clone;
    }
}
