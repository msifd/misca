package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.utils.MiscaUtils;

import static msifeed.mc.misca.crabs.rules.DynamicEffect.EffectArgs.FLOAT;
import static msifeed.mc.misca.crabs.rules.DynamicEffect.EffectArgs.INT;

public abstract class DynamicEffect extends Effect {
    public abstract EffectArgs[] args();

    public abstract void init(Object[] args);

    // // // // // // // //

    public static class ConstDamage extends DynamicEffect {
        private int value;

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
        public boolean shouldApply(Stage stage, ActionResult target, ActionResult other) {
            return stage == Stage.ACTION;
        }

        @Override
        public void apply(Stage stage, ActionResult target, ActionResult other) {
            target.damageToReceive += value;
        }
    }

    public static class Score extends DynamicEffect {
        private int value;

        @Override
        public String name() {
            return "score";
        }

        @Override
        public String toString() {
            return MiscaUtils.l10n("misca.crabs.buff.score", value);
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
        public boolean shouldApply(Stage stage, ActionResult target, ActionResult other) {
            return stage == Stage.BEFORE_MODS;
        }

        @Override
        public void apply(Stage stage, ActionResult target, ActionResult other) {
            target.effectMod += value;
        }
    }

    public static class MinScore extends DynamicEffect {
        private int value;

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
        public boolean shouldApply(Stage stage, ActionResult target, ActionResult other) {
            return stage == Stage.AFTER_MODS;
        }

        @Override
        public void apply(Stage stage, ActionResult target, ActionResult other) {
            target.actionSuccessful = target.totalSum >= value;
        }
    }

    public static class ReceivedDamageMultiplier extends DynamicEffect {
        private float value;

        @Override
        public String name() {
            return "received_damage_mult";
        }

        @Override
        public EffectArgs[] args() {
            return new EffectArgs[]{FLOAT};
        }

        @Override
        public void init(Object[] args) {
            value = (float) args[0];
        }

        @Override
        public boolean shouldApply(Stage stage, ActionResult target, ActionResult other) {
            return stage == Stage.AFTER_ACTION;
        }

        @Override
        public void apply(Stage stage, ActionResult target, ActionResult other) {
            target.damageToReceive *= value;
        }
    }

    // // // // // // // //

    public enum EffectArgs {
        INT, FLOAT, EFFECT
    }
}
