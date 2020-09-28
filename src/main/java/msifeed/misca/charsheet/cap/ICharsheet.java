package msifeed.misca.charsheet.cap;

public interface ICharsheet {
    String getName();
    void setName(String name);

    void replaceWith(ICharsheet charsheet);
    ICharsheet clone();
}