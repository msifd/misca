package msifeed.misca.charsheet;

import net.minecraft.client.resources.I18n;

public enum CharEffort {
    impact,
    knowledge,
    reflection,
    confidence,
    reputation;

    public String tr() {
        return I18n.format("enum.misca.effort." + name());
    }
}
