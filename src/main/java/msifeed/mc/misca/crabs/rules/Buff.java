package msifeed.mc.misca.crabs.rules;

import static msifeed.mc.misca.crabs.rules.DynamicEffect.EffectArgs.EFFECT;
import static msifeed.mc.misca.crabs.rules.DynamicEffect.EffectArgs.INT;

public final class Buff extends DynamicEffect {
    private int counter;
    private int stopAt;
    public Effect effect;

    @Override
    public String name() {
        return "buff";
    }

    @Override
    public Stage getStage() {
        return effect.getStage();
    }

    @Override
    public void apply(ActionResult self, ActionResult target) {
        if (enabled()) effect.apply(self, target);
    }

    public boolean enabled() {
        return counter >= 0 && counter <= stopAt;
    }

    public boolean disabled() {
        return counter > stopAt;
    }

    public void step() {
        counter++;
    }

    @Override
    public EffectArgs[] args() {
        return new EffectArgs[]{INT, INT, EFFECT};
    }

    @Override
    public void init(Object[] args) {
        this.counter = (int) args[0];
        this.stopAt = (int) args[1];
        this.effect = (Effect) args[2];
    }
}
