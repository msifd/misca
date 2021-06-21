package msifeed.misca.charstate.handler;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharNeed;
import msifeed.misca.charstate.CharstateConfig;
import msifeed.misca.charstate.cap.CharstateProvider;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;

public class CorruptionHandler {
    public static final IAttribute CORRUPTION = new RangedAttribute(null, Misca.MODID + ".corruption", 0, 0, 100).setShouldWatch(true);

    public void handleTime(EntityPlayer player, long absSecs, long relSecs) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;

        final IAttributeInstance inst = player.getEntityAttribute(CORRUPTION);

        final long passedInSilence = CharstateProvider.get(player).passedInSilence();
        if (passedInSilence > config.corruptionSilenceToGainSec) {
            final double gain = relSecs * config.corruptionGainPerSec * CharNeed.COR.gainFactor(player);
            inst.setBaseValue(CORRUPTION.clampValue(inst.getBaseValue() + gain));
        } else {
            final double lost = absSecs * config.corruptionLostPerSec * CharNeed.COR.lostFactor(player);
            inst.setBaseValue(CORRUPTION.clampValue(inst.getBaseValue() - lost));
        }
    }

    public void handleSpeech(EntityPlayer listener) {
        CharstateProvider.get(listener).resetSilenceTime();
    }

    public static boolean isPotionsDisabled(EntityPlayer player, CharNeed need) {
        if (need == CharNeed.COR) return false;

        final CharstateConfig config = Misca.getSharedConfig().charstate;
        return player.getEntityAttribute(CORRUPTION).getAttributeValue() >= config.corruptionDisablePotionsThreshold;
    }

    public static boolean isSkillsDisabled(EntityPlayer player) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        return player.getEntityAttribute(CORRUPTION).getAttributeValue() >= config.corruptionDisableSkillsThreshold;
    }

    public static boolean isCraftDisabled(EntityPlayer player) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        return player.getEntityAttribute(CORRUPTION).getAttributeValue() >= config.corruptionDisableCraft;
    }
}
