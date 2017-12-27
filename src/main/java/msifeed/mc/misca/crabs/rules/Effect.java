package msifeed.mc.misca.crabs.rules;

public abstract class Effect {
    public abstract String name();

    public abstract boolean shouldApply(Stage stage, ActionResult target, ActionResult other);

    public abstract void apply(Stage stage, ActionResult target, ActionResult other);

    @Override
    public String toString() {
        return name();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }

    // // // // // // // //

    public static class Damage extends Effect {
        @Override
        public boolean shouldApply(Stage stage, ActionResult target, ActionResult other) {
            return stage == Stage.ACTION;
        }

        @Override
        public void apply(Stage stage, ActionResult target, ActionResult other) {
            target.damageToReceive += other.ctx.damageDealt;
        }

        @Override
        public String name() {
            return "damage";
        }
    }

    // // // // // // // //

    public enum Stage {
        BEFORE_MODS, AFTER_MODS, ACTION, AFTER_ACTION
    }
}
