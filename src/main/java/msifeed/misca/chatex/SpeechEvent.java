package msifeed.misca.chatex;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.ServerChatEvent;

public class SpeechEvent extends ServerChatEvent {
    public SpeechEvent(EntityPlayerMP player, String message, ITextComponent component) {
        super(player, message, component);
    }
}
