package msifeed.misca.charsheet.cap;

import java.util.EnumMap;

public class Charsheet implements ICharsheet {
    private boolean isPlayer = false;
    private String name = "";
    private EnumMap<CharAttribute, Integer> attributes = new EnumMap<>(CharAttribute.class);
    private EnumMap<CharCounter, Integer> counters = new EnumMap<>(CharCounter.class);

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
    public int getAttribute(CharAttribute attr) {
        return attributes.getOrDefault(attr, 0);
    }

    @Override
    public void setAttribute(CharAttribute attr, int value) {
        attributes.put(attr, value);
    }

    @Override
    public int getCounter(CharCounter ctr) {
        return counters.getOrDefault(ctr, 0);
    }

    @Override
    public void setCounter(CharCounter ctr, int value) {
        counters.put(ctr, value);
    }

    @Override
    public void replaceWith(ICharsheet charsheet) {
        name = charsheet.getName();
        for (CharAttribute feat : CharAttribute.values())
            attributes.put(feat, charsheet.getAttribute(feat));
        for (CharCounter feat : CharCounter.values())
            counters.put(feat, charsheet.getCounter(feat));
    }

    @Override
    public ICharsheet clone() {
        final Charsheet clone;
        try {
            clone = (Charsheet) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }

        clone.name = name;
        clone.attributes = attributes.clone();
        clone.counters = counters.clone();

        return clone;
    }
}
