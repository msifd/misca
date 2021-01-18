package msifeed.misca.chatex;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.ServerChatEvent;

public class SpeechEvent extends ServerChatEvent {
    public final int range;

    public SpeechEvent(EntityPlayerMP player, int range, String message, ITextComponent component) {
        super(player, message, component);
        this.range = range;
    }
}
