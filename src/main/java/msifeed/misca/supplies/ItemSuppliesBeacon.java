package msifeed.misca.supplies;

import msifeed.misca.Misca;
import msifeed.misca.MiscaThings;
import msifeed.misca.charstate.handler.StaminaHandler;
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
        setTranslationKey(ID);
    }

    public static ItemStack createBeaconItem(ISuppliesInvoice invoice) {
        final ItemStack beacon = new ItemStack(MiscaThings.suppliesBeacon);
        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(beacon);
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

        final long deliveryIndex = supplies.getLastDeliveryIndex();
        final List<ItemStack> delivery = BackgroundSupplies.gatherDelivery(supplies);

        StaminaHandler.consumeSuppliesDelivery(player, delivery);
        delivery.forEach(player::addItemStackToInventory); // empties items in delivery list

        final EnumActionResult result = deliveryIndex != supplies.getLastDeliveryIndex()
                ? EnumActionResult.SUCCESS
                : EnumActionResult.PASS;

        return new ActionResult<>(result, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        final ISuppliesInvoice invoice = SuppliesInvoiceProvider.get(stack);
        if (invoice == null) {
            tooltip.add("Can't get supplies info");
            return;
        }

        tooltip.addAll(BackgroundSupplies.getRelativeInfoLines(invoice));
    }
}
