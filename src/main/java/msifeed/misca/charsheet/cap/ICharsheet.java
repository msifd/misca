package msifeed.misca.charsheet.cap;

public interface ICharsheet {
    String getName();
    void setName(String name);

    int getAbility(CharAbility abi);
    void setAbility(CharAbility abi, int value);

    int getCounter(CharCounter ctr);
    void setCounter(CharCounter ctr, int value);

    void replaceWith(ICharsheet charsheet);
    ICharsheet clone();
}