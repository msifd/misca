package msifeed.mc.misca.crabs.rules;

import static msifeed.mc.misca.crabs.rules.DynamicEffect.EffectArgs.EFFECT;
import static msifeed.mc.misca.crabs.rules.DynamicEffect.EffectArgs.INT;

public final class Buff extends DynamicEffect {
    public Effect effect;
    private int counter;
    private int stopAt;

    @Override
    public String name() {
        return "buff";
    }

    @Override
    public String toString() {
        return "(" + (counter - stopAt) + ")"
                + effect.toString();
    }

    @Override
    public boolean shouldApply(Stage stage, ActionResult target, ActionResult other) {
        return effect.shouldApply(stage, target, other);
    }

    @Override
    public void apply(Stage stage, ActionResult target, ActionResult other) {
        if (active()) {
            effect.apply(stage, target, other);
            step();
        }
    }

    public boolean active() {
        return started() && !ended();
    }

    public boolean started() {
        return counter >= 0;
    }

    public boolean ended() {
        return counter > stopAt;
    }

    public void step() {
        counter++;
    }

    /**
     * [ходов до начала эффекта], [длительность эффекта], [эффект]
     * Длительность - сколько ходов переживет бафф после активации.
     */
    @Override
    public EffectArgs[] args() {
        return new EffectArgs[]{INT, INT, EFFECT};
    }

    @Override
    public void init(Object[] args) {
        this.counter = -((int) args[0]); // Негативничаем, т.к. это прямой отсчет до нуля и далее до stopAt
        this.stopAt = (int) args[1];
        this.effect = (Effect) args[2];
    }
}
