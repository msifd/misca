package msifeed.misca.charsheet.cap;

import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charsheet.CharResource;
import msifeed.misca.charsheet.CharSkill;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.sys.cap.IntContainer;

public class CharsheetImpl implements ICharsheet {
    private String name = "";
    private String wikiPage = "";
    private final IntContainer<CharSkill> skills = new IntContainer<>(CharSkill.class, 0, 25);
    private final IntContainer<CharEffort> effortPools = new IntContainer<>(CharEffort.class, 0, 50);
    private final IntContainer<CharResource> resources = new IntContainer<>(CharResource.class, 0, 1000);

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
        name = charsheet.getName();
        wikiPage = charsheet.getWikiPage();
        skills.replaceWith(charsheet.skills());
        effortPools.replaceWith(charsheet.effortPools());
        resources.replaceWith(charsheet.resources());
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
