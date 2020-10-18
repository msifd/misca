package msifeed.misca.charsheet.cap;

public interface ICharsheet {
    boolean isPlayer();
    void markPlayer();

    String getName();
    void setName(String name);

    int getAttribute(CharAttribute abi);
    void setAttribute(CharAttribute abi, int value);

    int getCounter(CharCounter ctr);
    void setCounter(CharCounter ctr, int value);

    void replaceWith(ICharsheet charsheet);
    ICharsheet clone();
}