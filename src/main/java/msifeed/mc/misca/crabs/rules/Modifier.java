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

    // // // // // // // //

    public static class DiceG30 extends Modifier {
        @Override
        public int mod(Character c) {
            return DiceMath.g30();
        }

        @Override
        public boolean isDice() {
            return true;
        }

        @Override
        public String toString() {
            return "g30";
        }
    }

    public static class DiceG30Plus extends DiceG30 {
        public int mod(Character c) {
            return DiceMath.g30_plus();
        }

        @Override
        public String toString() {
            return "g30+";
        }
    }

    public static class DiceG30Minus extends DiceG30 {
        @Override
        public int mod(Character c) {
            return DiceMath.g30_minus();
        }

        @Override
        public String toString() {
            return "g30-";
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
        public String toString() {
            return stat.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && this.stat.equals(((Stat) obj).stat);
        }
    }

    public static class Const extends Modifier {
        private final int constant;

        public Const(int c) {
            this.constant = c;
        }

        @Override
        public int mod(Character c) {
            return constant;
        }

        @Override
        public String toString() {
            return Integer.toString(constant);
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) && this.constant == ((Const) obj).constant;
        }
    }
}