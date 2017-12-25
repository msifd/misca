package msifeed.mc.misca.crabs.rules;

public abstract class Effect {
    public abstract String name();

    public abstract boolean apply(Stage stage, ActionResult target, ActionResult other);

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
        public boolean apply(Stage stage, ActionResult target, ActionResult other) {
            if (stage != Stage.RESULT) return false;
            target.damageToReceive += other.ctx.damageDealt;
            return true;
        }

        @Override
        public String name() {
            return "damage";
        }
    }

    // // // // // // // //

    public enum Stage {
        BEFORE_MODS, AFTER_MODS, RESULT, AFTER_RESULT
    }
}
