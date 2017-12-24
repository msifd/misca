package msifeed.mc.misca.crabs.rules;

import static msifeed.mc.misca.crabs.rules.DynamicEffect.EffectArgs.INT;

public abstract class DynamicEffect extends Effect {
    public abstract EffectArgs[] args();

    public abstract void init(Object[] args);

    // // // // // // // //

    public static class ConstDamage extends DynamicEffect {
        public int value;

        @Override
        public String name() {
            return "const_damage";
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
        public boolean apply(Stage stage, ActionResult target, ActionResult other) {
            if (stage != Stage.RESULT) return false;
            target.damageToReceive += value;
            return true;
        }
    }

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
        public boolean apply(Stage stage, ActionResult target, ActionResult other) {
            if (stage != Stage.BEFORE_MODS) return false;
            target.effectMod += value;
            return true;
        }
    }

    public static class MinScore extends DynamicEffect {
        public int value;

        @Override
        public String name() {
            return "min_score";
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
        public boolean apply(Stage stage, ActionResult target, ActionResult other) {
            if (stage != Stage.AFTER_MODS) return false;
            target.actionSuccessful = target.totalSum >= value;
            return true;
        }
    }

    // // // // // // // //

    public enum EffectArgs {
        INT, EFFECT
    }
}
