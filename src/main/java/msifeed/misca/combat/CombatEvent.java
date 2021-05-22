package msifeed.misca.combat;

import net.minecraft.util.text.translation.I18n;

public enum CombatEvent {
    hit, critHit, evade, critEvade,
    neutralDamage, magicBackfire,
    death;

    public boolean isGood(boolean isMe) {
        switch (this) {
            case hit:
            case critHit:
            case evade:
            case critEvade:
                return isMe;
            default:
                return false;
        }
    }

    public String tr() {
        return I18n.translateToLocal("enum.misca.combat.event." + name());
    }
}
