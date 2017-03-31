package ru.ariadna.misca.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;

/**
 * Отменяет рендеринг плашки с именем над игроком
 */
public class HideNametag {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLivingSpecial(RenderLivingEvent.Specials.Pre event) {
        if (event.entity instanceof EntityPlayer && event.isCancelable()) {
            event.setCanceled(true);
        }
    }
}
