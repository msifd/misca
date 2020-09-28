package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.cap.Charsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.CharsheetStorage;
import msifeed.misca.charsheet.cap.ICharsheet;
import msifeed.misca.charsheet.client.CharsheetClientRpc;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class CharsheetHandler {
    public static final ResourceLocation CAP = new ResourceLocation(Misca.MODID, "char");

    public void preInit() {
        CapabilityManager.INSTANCE.register(ICharsheet.class, new CharsheetStorage(), Charsheet::new);
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);

        Misca.RPC.register(new CharsheetServerRpc());
        if (FMLCommonHandler.instance().getSide().isClient())
            Misca.RPC.register(new CharsheetClientRpc());
    }

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(CAP, new CharsheetProvider());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player.world.isRemote)
            return;

        syncCharsheet((EntityPlayerMP) event.player, event.player);
    }

    @SubscribeEvent
    public void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player.world.isRemote)
            return;

        syncCharsheet((EntityPlayerMP) event.player, event.player);
    }

    @SubscribeEvent
    public void onPlayerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player.world.isRemote)
            return;

        syncCharsheet((EntityPlayerMP) event.player, event.player);
    }

    @SubscribeEvent
    public void onPlayerTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof EntityPlayer))
            return;
        syncCharsheet((EntityPlayerMP) event.getEntityPlayer(), (EntityPlayer) event.getTarget());
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        final ICharsheet original = CharsheetProvider.get(event.getOriginal());
        final ICharsheet cloned = CharsheetProvider.get(event.getEntityPlayer());
        cloned.replaceWith(original);
    }

    private void syncCharsheet(EntityPlayerMP receiver, EntityPlayer target) {
        final NBTTagCompound nbt = CharsheetProvider.encode(CharsheetProvider.get(target));
        Misca.RPC.sendTo(receiver, ICharsheetRpc.sync, target.getUniqueID(), nbt);
    }
}
