package msifeed.misca.charstate.handler;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charsheet.CharSkill;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charstate.CharstateConfig;
import msifeed.misca.charstate.cap.CharstateProvider;
import msifeed.sys.cap.FloatContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class EffortsHandler {
    public void handleTime(EntityPlayer player, long secs) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final float factor = 1 + CharsheetProvider.get(player).skills().get(CharSkill.survival) * config.survivalSkillEffortsRestFactor;
        final float restored = secs * config.effortRestPerSec * factor;

        final ICharsheet sheet = CharsheetProvider.get(player);
        final FloatContainer<CharEffort> efforts = CharstateProvider.get(player).efforts();

        for (CharEffort eff : CharEffort.values()) {
            final int max = sheet.effortPools().get(eff);
            efforts.set(eff, MathHelper.clamp(efforts.get(eff) + restored, 0, max));
        }
    }
}
