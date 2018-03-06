package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.Stats;

public abstract class Modifier {
    Modifier() {
    }

    public abstract int mod(Character c);

    public boolean isDice() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }

    public abstract String name();

    // // // // // // // //

    public static class DiceG40 extends Modifier {
        @Override
        public int mod(Character c) {
            return DiceMath.g40();
        }

        @Override
        public boolean isDice() {
            return true;
        }

        @Override
        public String name() {
            return "g40";
        }
    }

    public static class DiceG40Plus extends DiceG40 {
        public int mod(Character c) {
            return DiceMath.g40_plus();
        }

        @Override
        public String name() {
            return "g40+";
        }
    }

    public static class DiceG40Minus extends DiceG40 {
        @Override
        public int mod(Character c) {
            return DiceMath.g40_minus();
        }

        @Override
        public String name() {
            return "g40-";
        }
    }

    public static class Stat extends Modifier {
        private final Stats stat;

        public Stat(Stats stat) {
            this.stat = stat;
        }

        @Override
        public int mod(Character c) {
            return c.stat(stat);
        }

        @Override
        public String name() {
            return stat.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && this.stat.equals(((Stat) obj).stat);
        }
    }
}
