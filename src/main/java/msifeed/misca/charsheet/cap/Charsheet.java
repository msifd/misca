package msifeed.misca.charsheet.cap;

import java.util.EnumMap;

public class Charsheet implements ICharsheet {
    private String name = "";
    private EnumMap<CharAbility, Integer> abilities = new EnumMap<>(CharAbility.class);
    private EnumMap<CharCounter, Integer> counters = new EnumMap<>(CharCounter.class);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getAbility(CharAbility abi) {
        return abilities.getOrDefault(abi, 0);
    }

    @Override
    public void setAbility(CharAbility abi, int value) {
        abilities.put(abi, value);
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
        for (CharAbility feat : CharAbility.values())
            abilities.put(feat, charsheet.getAbility(feat));
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
        clone.abilities = abilities.clone();
        clone.counters = counters.clone();

        return clone;
    }
}
