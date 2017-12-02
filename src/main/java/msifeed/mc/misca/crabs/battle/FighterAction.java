package msifeed.mc.misca.crabs.battle;

import java.io.Serializable;

public class FighterAction implements Serializable {
    Type type;

    public FighterAction(Type type) {
        this.type = type;
    }

    public enum Type {
        JOIN, LEAVE
    }
}
