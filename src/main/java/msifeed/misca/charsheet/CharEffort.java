package msifeed.misca.charsheet;

import net.minecraft.util.text.translation.I18n;

public enum CharEffort {
    impact,
    knowledge,
    reflection,
    confidence,
    reputation;

    public String tr() {
        return I18n.translateToLocal("enum.misca.effort." + name());
    }
}
