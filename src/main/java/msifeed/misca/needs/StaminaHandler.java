package msifeed.misca.needs;

import msifeed.misca.Misca;
import msifeed.misca.needs.cap.IPlayerNeeds;
import msifeed.misca.needs.cap.PlayerNeedsProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;

public class StaminaHandler {
    public void handleTime(IPlayerNeeds needs, long secs) {
        final NeedsConfig config = Misca.getSharedConfig().needs;
        final double restored = secs * config.staminaRestPerSec;

        if (needs.passedFromMining() > 2)
            needs.add(IPlayerNeeds.NeedType.stamina, restored);
    }

    public void handleMining(PlayerEvent.BreakSpeed event, IPlayerNeeds needs) {
        final NeedsConfig config = Misca.getSharedConfig().needs;
        needs.resetMiningTime();
        needs.add(IPlayerNeeds.NeedType.stamina, -config.staminaCostPerMiningTick);

        final double stamina = needs.get(IPlayerNeeds.NeedType.stamina);
        event.setNewSpeed((float) (event.getNewSpeed() * config.globalMiningSpeedModifier * stamina));

        if (event.getEntityPlayer().world.isRemote && event.getEntityPlayer().ticksExisted % 10 == 0) {
            System.out.printf("stamina %.5f\n", stamina);
        }
    }

    public static void consumeSuppliesDelivery(EntityPlayer player, List<ItemStack> delivery) {
        final NeedsConfig config = Misca.getSharedConfig().needs;
        final IPlayerNeeds needs = PlayerNeedsProvider.get(player);
        final int items = delivery.stream().mapToInt(ItemStack::getCount).sum();
        final double lost = items * config.staminaCostPerSupplyItem;
        needs.add(IPlayerNeeds.NeedType.stamina, -lost);
    }
}
