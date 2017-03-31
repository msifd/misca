package ru.ariadna.misca.chat;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.ServerChatEvent;

import java.util.regex.Pattern;

/**
 * Форматируем сообщения по шаблону.
 * Например сообщения в слешах делаются серыми чтобы отличить оффтоп
 */
public class ChatFormat {
    private static final Pattern OFFTOP_PATTERN = Pattern.compile("(^\\\\.*\\\\$)|(^\\\\{2}.*$)");
    private static final EnumChatFormatting OFFTOP_COLOR = EnumChatFormatting.GRAY;

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onServerChatEvent(ServerChatEvent event) {
        processOfftop(event);
    }

    private void processOfftop(ServerChatEvent event) {
        if (OFFTOP_PATTERN.matcher(event.message).matches()) {
            // FIXME ChatComponentTranslation has no getChatStyle()
//            ChatStyle style = event.component.getChatStyle();
            ChatStyle style = new ChatStyle();
            style.setColor(OFFTOP_COLOR);
            event.component.setChatStyle(style);
        }
    }
}
