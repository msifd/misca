package msifeed.mc.misca.crabs.rules;

import static msifeed.mc.misca.crabs.rules.DynamicEffect.EffectArgs.INT;

public abstract class DynamicEffect extends Effect {
    public abstract EffectArgs[] args();

    public abstract void init(Object[] args);

    // // // // // // // //

    public static class Score extends DynamicEffect {
        public int value;

        @Override
        public String name() {
            return "score";
        }

        @Override
        public EffectArgs[] args() {
            return new EffectArgs[]{INT};
        }

        @Override
        public void init(Object[] args) {
            value = (int) args[0];
        }

        @Override
        public void apply(ActionResult self, ActionResult target) {
            self.totalSum += value;
        }
    }

    // // // // // // // //

    public enum EffectArgs {
        INT, EFFECT
    }
}
