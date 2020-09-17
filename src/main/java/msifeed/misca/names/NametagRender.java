package msifeed.misca.names;

import msifeed.misca.chatex.client.TypingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NametagRender {
    private static final int NAME_VISIBILITY_RANGE = 4;
    private static final int TYPING_VISIBILITY_RANGE = 15;
    private static final int TYPING_TAG_INTERVAL_MS = 400;
    private static final String[] TYPING_REPLACER = {"..", "...", "...."};

    @SubscribeEvent
    public void onRenderLivingSpecialPre(RenderLivingEvent.Specials.Pre event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        final EntityPlayer self = Minecraft.getMinecraft().player;
        final EntityPlayer player = (EntityPlayer) event.getEntity();

//        final Long typingStarted = typingPlayers.get(player.getEntityId());
//        boolean isTyping = false;
//        if (typingStarted != null && !displayOriginalUsername()) {
//            final long now = System.currentTimeMillis();
//            if (now - typingStarted > TYPING_PING_MS) {
//                // End typing
//                typingPlayers.remove(player.getEntityId());
//                player.refreshDisplayName();
//            } else {
//                // Refresh typing
//                final String dots = TYPING_REPLACER[(int) (now / TYPING_TAG_INTERVAL_MS % TYPING_REPLACER.length)];
//                ReflectionHelper.setPrivateValue(EntityPlayer.class, player, dots, "displayname");
//                isTyping = true;
//            }
//        } else {
//            // Show nicknames
//            final String name = displayOriginalUsername() ? player.getCommandSenderName() : getPreferredName(player);
//            if (!name.equals(player.getDisplayName()))
//                ReflectionHelper.setPrivateValue(EntityPlayer.class, player, name, "displayname");
//        }
//
        final int visibilityRange = TypingHandler.isTyping(player)
                ? TYPING_VISIBILITY_RANGE : NAME_VISIBILITY_RANGE;
        if (self.getDistance(player) > visibilityRange)
            event.setCanceled(true);
    }
}
