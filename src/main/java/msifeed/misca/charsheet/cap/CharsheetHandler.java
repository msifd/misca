package msifeed.misca.charsheet.cap;

import msifeed.misca.Misca;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CharsheetHandler {
    public static final ResourceLocation CAP = new ResourceLocation(Misca.MODID, "char");

    public void preInit() {
        CapabilityManager.INSTANCE.register(ICharsheet.class, new CharsheetStorage(), Charsheet::new);
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof EntityPlayer)) return;

        event.addCapability(CAP, new CharsheetProvider());
    }
}
