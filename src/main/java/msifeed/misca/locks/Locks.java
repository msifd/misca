package msifeed.misca.locks;

import msifeed.misca.Misca;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Locks {
    public static final ResourceLocation CAP = new ResourceLocation(Misca.MODID, "lock");

    public void preInit() {
        CapabilityManager.INSTANCE.register(ILockable.class, new LockableStorage(), Lockable::new);
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<TileEntity> event) {
        // TODO: lock doors too!
        if (event.getObject() instanceof IInventory)
            event.addCapability(CAP, new LockableProvider());
    }

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        final TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        if (tile == null) return;

        final ILockable lock = LockableProvider.get(tile);
        if (lock == null) return;

        if (lock.isLocked()) {
            event.setCanceled(true);

            final ITextComponent te = new TextComponentString("Locked!");
            te.getStyle().setColor(TextFormatting.YELLOW);
            event.getEntityPlayer().sendStatusMessage(te, true);
        }
    }
}
