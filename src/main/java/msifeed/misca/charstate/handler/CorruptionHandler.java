package msifeed.misca.charstate.handler;

import msifeed.misca.Misca;
import msifeed.misca.charstate.CharstateConfig;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;

public class CorruptionHandler {
    public static final IAttribute CORRUPTION = new RangedAttribute(null, Misca.MODID + ".corruption", 0, 0, 100).setShouldWatch(true);

    public void handleTime(EntityPlayer player, long secs) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final double lost = secs * config.corruptionLostPerSec;

        final IAttributeInstance inst = player.getEntityAttribute(CORRUPTION);
        inst.setBaseValue(CORRUPTION.clampValue(inst.getBaseValue() - lost));
    }
}
