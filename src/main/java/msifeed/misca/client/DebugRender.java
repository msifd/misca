package msifeed.misca.client;

import com.google.gson.GsonBuilder;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.tools.ItemDebugTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DebugRender {
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;

        final EntityPlayer self = Minecraft.getMinecraft().player;
        if (!(self.getHeldItemMainhand().getItem() instanceof ItemDebugTool)) return;

        final RayTraceResult rayTraceResult = Minecraft.getMinecraft().objectMouseOver;
        final EntityLivingBase target;
        if (rayTraceResult != null && rayTraceResult.entityHit instanceof EntityLivingBase)
            target = (EntityLivingBase) rayTraceResult.entityHit;
        else
            target = self;

//        final ICharsheet charsheet = CharsheetProvider.get(target);
//        final String json = new GsonBuilder().setPrettyPrinting().create().toJson(charsheet);
//
//        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
//        int yOffset = fr.FONT_HEIGHT;
//        for (String line : json.split("\n")) {
//            fr.drawString(line, 10, yOffset, 0xffffff);
//            yOffset += fr.FONT_HEIGHT;
//        }
    }
}
