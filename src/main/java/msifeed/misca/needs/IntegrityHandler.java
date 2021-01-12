package msifeed.misca.needs;

import msifeed.misca.Misca;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IntegrityHandler {
    public static final IAttribute INTEGRITY = new RangedAttribute(null, Misca.MODID + ".integrity", 100, 0, 100).setShouldWatch(true);

    private final Potion slowness = Potion.getPotionById(2);
    private final Potion miningFatigue = Potion.getPotionById(4);
    private final Potion weakness = Potion.getPotionById(18);

    public void handle(EntityPlayer player, IAttributeInstance attr, long secs) {
        final double intPerSec = 3d / (5 * 60 * 60);
//        final double intPerSec = 3d / (5);
        final double restored = secs * intPerSec;
        final double value = attr.getAttribute().clampValue(attr.getBaseValue() + restored);

        attr.setBaseValue(value);
//        System.out.printf("int restored: %.5f, base: %.5f\n", restored, attr.getBaseValue());

        if (value <= 75) addPotionEffect(player, miningFatigue, value <= 50 ? 1 : 0);
        if (value <= 50) addPotionEffect(player, weakness, value <= 25 ? 1 : 0);
        if (value <= 25) addPotionEffect(player, slowness, 0);
    }

    private static void addPotionEffect(EntityPlayer player, Potion potion, int amplification) {
        final PotionEffect effect = new PotionEffect(potion, 120, amplification, true, false);
        player.addPotionEffect(effect);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerHurt(LivingHurtEvent event) {
        if (event.isCanceled() || event.getAmount() < 1) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        if (event.getEntity().world.isRemote) return;
        if (event.getSource().canHarmInCreative()) return;

        final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        final IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(INTEGRITY);

        final double lost = event.getAmount();
//        final double lost = event.getAmount() * 5;
        attr.setBaseValue(attr.getAttribute().clampValue(attr.getBaseValue() - lost));
//        System.out.printf("int lost: %.5f\n", lost);
    }
}
