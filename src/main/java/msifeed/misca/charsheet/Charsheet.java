package msifeed.misca.charsheet;

public class Charsheet implements ICharsheet {
    private boolean isPlayer = false;
    private String name = "";
    private String wikiPage = "";
    private final ValueContainer<CharAttribute> attributes = new ValueContainer<>(CharAttribute.class, 0, 25);
    private final ValueContainer<CharResource> resources = new ValueContainer<>(CharResource.class, 0, 1000);

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
    public ValueContainer<CharAttribute> attrs() {
        return attributes;
    }

    @Override
    public ValueContainer<CharResource> resources() {
        return resources;
    }

    @Override
    public void replaceWith(ICharsheet charsheet) {
        name = charsheet.getName();
        wikiPage = charsheet.getWikiPage();
        attributes.replaceWith(charsheet.attrs());
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
