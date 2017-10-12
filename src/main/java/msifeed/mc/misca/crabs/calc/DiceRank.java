package msifeed.mc.misca.crabs.calc;

public enum DiceRank {
    REGULAR, LUCK, FAIL;

    public static DiceRank of(int roll) {
        if (roll >= 28) return LUCK;
        else if (roll <= 3) return FAIL;
        else return REGULAR;
    }
}
