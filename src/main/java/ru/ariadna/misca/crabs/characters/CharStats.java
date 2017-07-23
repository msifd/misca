package ru.ariadna.misca.crabs.characters;

public enum CharStats {
    STRENGTH, REFLEXES, PERCEPTION, INTELLIGENCE, DETERMINATION, SPIRIT;

    public static CharStats fromShort(String s) {
        switch (s.toUpperCase()) {
            case "STR":
                return STRENGTH;
            case "REF":
                return REFLEXES;
            case "PER":
                return PERCEPTION;
            case "INT":
                return INTELLIGENCE;
            case "DET":
                return DETERMINATION;
            case "SPR":
                return SPIRIT;
            default:
                return null;
        }
    }

    public String pretty() {
        switch (this) {
            case SPIRIT:
                return "SPR";
            default:
                return toString().substring(0, 3);
        }
    }
}
