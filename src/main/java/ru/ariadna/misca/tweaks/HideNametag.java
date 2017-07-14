package ru.ariadna.misca.tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;

/**
 * Отменяет рендеринг плашки с именем над игроком дальше 3 блоков
 */
public class HideNametag {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLivingSpecial(RenderLivingEvent.Specials.Pre event) {
        EntityPlayer self = Minecraft.getMinecraft().thePlayer;
        if (event.entity instanceof EntityPlayer && event.isCancelable() && self.getDistanceToEntity(event.entity) > 3) {
            event.setCanceled(true);
        }
    }
}
