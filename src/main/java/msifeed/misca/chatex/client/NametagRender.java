package msifeed.misca.chatex.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NametagRender {
    private static final int NAME_VISIBILITY_RANGE = 4;
    private static final int TYPING_DOTS_INTERVAL_MS = 400;
    private static final String[] TYPING_DOTS = {"..  ", "... ", "...."};

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderNameTag(RenderLivingEvent.Specials.Pre<EntityPlayer> event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        final EntityPlayer self = Minecraft.getMinecraft().player;
        final EntityPlayer player = (EntityPlayer) event.getEntity();

        if (TypingHandler.isTyping(player)) {
            event.setCanceled(true);
            final int dotsIndex = (int) (System.currentTimeMillis() % 10000 / TYPING_DOTS_INTERVAL_MS);
            final String dots = TYPING_DOTS[dotsIndex];
            event.getRenderer().renderLivingLabel(player, dots, event.getX(), event.getY(), event.getZ(), 64);
        } else {
            if (self.getDistance(player) > NAME_VISIBILITY_RANGE)
                event.setCanceled(true);
        }
    }
}
