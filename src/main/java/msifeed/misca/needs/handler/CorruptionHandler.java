package msifeed.misca.needs.handler;

import msifeed.misca.Misca;
import msifeed.misca.needs.NeedsConfig;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;

public class CorruptionHandler {
    public static final IAttribute CORRUPTION = new RangedAttribute(null, Misca.MODID + ".corruption", 0, 0, 100);

    public void handleTime(EntityPlayer player, long secs) {
        final NeedsConfig config = Misca.getSharedConfig().needs;
        final double lost = secs * config.corruptionLostPerSec;

        final IAttributeInstance inst = player.getEntityAttribute(CORRUPTION);
        inst.setBaseValue(CORRUPTION.clampValue(inst.getBaseValue() - lost));
    }
}
