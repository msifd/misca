package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.crabs.calc.DiceMath;
import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.Stats;

public abstract class Roll {
    private int cache;

    protected abstract int value(Character c);

    public int roll(Character c) {
        return this.cache = value(c);
    }

    public int getRoll() {
        return cache;
    }

    public boolean isDice() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }

    // // // // // // // //

    public static class DiceG30 extends Roll {
        protected int value(Character c) {
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
        protected int value(Character c) {
            return DiceMath.g30_plus();
        }

        @Override
        public String toString() {
            return "g30+";
        }
    }

    public static class DiceG30Minus extends DiceG30 {
        protected int value(Character c) {
            return DiceMath.g30_minus();
        }

        @Override
        public String toString() {
            return "g30-";
        }
    }

    public static class Stat extends Roll {
        private final Stats stat;

        public Stat(Stats stat) {
            this.stat = stat;
        }

        @Override
        protected int value(Character c) {
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

    public static class Const extends Roll {
        private final int constant;

        public Const(int c) {
            this.constant = c;
        }

        @Override
        protected int value(Character c) {
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
