package msifeed.misca.supplies;

import msifeed.misca.Misca;
import msifeed.misca.MiscaThings;
import msifeed.misca.supplies.cap.ISuppliesInvoice;
import msifeed.misca.supplies.cap.SuppliesInvoiceProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSuppliesBeacon extends Item {
    public static final String ID = "supplies_beacon";

    public ItemSuppliesBeacon() {
        setRegistryName(Misca.MODID, ID);
        setUnlocalizedName(ID);
    }

    public static ItemStack createBeaconItem(ISuppliesInvoice invoice) {
        final ItemStack beacon = new ItemStack(MiscaThings.suppliesBeacon);
        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(beacon);
        if (supplies == null) return null;
        supplies.replaceWith(invoice);

        return beacon;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new SuppliesInvoiceProvider();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        final ItemStack stack = player.getHeldItem(hand);
        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(stack);
        if (supplies == null) return new ActionResult<>(EnumActionResult.FAIL, stack);

        final long deliveryTime = supplies.getLastDeliveryTime();
        BackgroundSupplies.deliver(supplies, player.inventory);

        final EnumActionResult result = deliveryTime != supplies.getLastDeliveryTime()
                ? EnumActionResult.SUCCESS
                : EnumActionResult.PASS;

        return new ActionResult<>(result, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(stack);
        if (supplies == null) {
            tooltip.add("Can't get supplies info");
            return;
        }

        final long interval = supplies.getDeliveryInterval();
        final long lastDelivery = supplies.getLastDeliveryTime();
        final long maxSequence = supplies.getMaxDeliverySequence();
        final long now = System.currentTimeMillis();
        final long suppliesLanded = Math.min((now - lastDelivery) / interval, maxSequence);
        final double nextSupply = (interval - (now - lastDelivery) % interval) / 60000d;
        final int stacks = (int) supplies.getProducts().stream()
                .mapToDouble(s -> Math.ceil(s.getCount() * suppliesLanded / (double) s.getMaxStackSize()))
                .sum();

        if (suppliesLanded < maxSequence)
            tooltip.add(String.format("Supplies %d/%d. Next in %.1f min", suppliesLanded, maxSequence, nextSupply));
        else
            tooltip.add(String.format("Supplies %d/%d", suppliesLanded, maxSequence));
        tooltip.add(String.format("Stacks of supplies: %d", stacks));
    }
}
