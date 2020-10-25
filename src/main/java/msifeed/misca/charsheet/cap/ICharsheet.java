package msifeed.misca.charsheet.cap;

public interface ICharsheet extends Cloneable {
    boolean isPlayer();
    void markPlayer();

    String getName();
    void setName(String name);

    ValueContainer<CharAttribute> attrs();
    ValueContainer<CharResource> resources();

    void replaceWith(ICharsheet charsheet);
    ICharsheet clone();
}