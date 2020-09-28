package msifeed.misca.charsheet.cap;

public class Charsheet implements ICharsheet {
    private String name = "";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void replaceWith(ICharsheet charsheet) {
        name = charsheet.getName();
    }

    @Override
    public ICharsheet clone() {
        final Charsheet clone = new Charsheet();
        clone.name = name;
        return clone;
    }
}
