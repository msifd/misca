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

    public static class DiceG30 extends Roll {
        protected int value(Character c) {
            return DiceMath.g30();
        }

        @Override
        public boolean isDice() {
            return true;
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
    }

    public static class Const extends Roll {
        private final int constant;

        public Const(int constant) {
            this.constant = constant;
        }

        @Override
        protected int value(Character c) {
            return constant;
        }
    }
}
