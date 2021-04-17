package msifeed.misca.supplies;

import msifeed.misca.Misca;
import msifeed.misca.supplies.cap.ISuppliesInvoice;
import msifeed.misca.supplies.cap.SuppliesInvoice;
import msifeed.misca.supplies.cap.SuppliesInvoiceProvider;
import msifeed.misca.supplies.cap.SuppliesInvoiceStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BackgroundSupplies {
    public static final ResourceLocation CAP = new ResourceLocation(Misca.MODID, "supplies");

    public void preInit() {
        CapabilityManager.INSTANCE.register(ISuppliesInvoice.class, new SuppliesInvoiceStorage(), SuppliesInvoice::new);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onAttachTileCapability(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject().hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
            event.addCapability(CAP, new SuppliesInvoiceProvider());
    }

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote) return;

        final TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        if (tile == null) return;
        if (!tile.hasCapability(SuppliesInvoiceProvider.CAP, null)) return;

        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(tile);
        if (supplies == null) return;

        final IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        final int max = event.getEntityPlayer().isSneaking() ? 1 : Integer.MAX_VALUE;
        SuppliesFlow.makeDelivery(event.getEntityPlayer(), supplies, inv, max);
    }
}
