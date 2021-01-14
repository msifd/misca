package msifeed.misca.needs.handler;

import msifeed.misca.Misca;
import msifeed.misca.needs.NeedsConfig;
import msifeed.misca.needs.cap.IPlayerNeeds;
import msifeed.misca.needs.cap.PlayerNeedsProvider;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;

public class StaminaHandler {
    public static final IAttribute STAMINA = new RangedAttribute(null, Misca.MODID + ".stamina", 1, 0, 1).setShouldWatch(true);

    public void handleTime(EntityPlayer player, long secs) {
        final NeedsConfig config = Misca.getSharedConfig().needs;
        final IPlayerNeeds needs = PlayerNeedsProvider.get(player);
        if (needs.passedFromMining() < config.staminaRestTimeoutSec) return;

        final double restored = secs * config.staminaRestPerSec;

        final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() + restored));
    }

    public void handleMining(PlayerEvent.BreakSpeed event) {
        final IPlayerNeeds needs = PlayerNeedsProvider.get(event.getEntityPlayer());

        final NeedsConfig config = Misca.getSharedConfig().needs;
        needs.resetMiningTime();

        final IAttributeInstance inst = event.getEntityPlayer().getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() - config.staminaCostPerMiningTick));

        final double stamina = inst.getBaseValue();
        event.setNewSpeed((float) (event.getNewSpeed() * config.globalMiningSpeedModifier * stamina));
        if (event.getEntityPlayer().world.isRemote)
            System.out.println(event.getNewSpeed());
    }

    public void handleCrafting(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
        final NeedsConfig config = Misca.getSharedConfig().needs;
        final double lost = event.crafting.getCount() * config.staminaCostPerSupplyItem;
        
        final IAttributeInstance inst = event.player.getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() - lost));
    }

    public static void consumeSuppliesDelivery(EntityPlayer player, List<ItemStack> delivery) {
        final NeedsConfig config = Misca.getSharedConfig().needs;
        final int items = delivery.stream().mapToInt(ItemStack::getCount).sum();
        final double lost = items * config.staminaCostPerSupplyItem;

        final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() - lost));
    }
}
