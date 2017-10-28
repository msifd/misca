package msifeed.mc.misca.crabs.rules;

public abstract class Effect {
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }

    public static class Damage extends Effect {
        @Override
        public String toString() {
            return "damage";
        }
    }
}
