package ru.ariadna.misca.chat;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.ServerChatEvent;

import java.util.regex.Pattern;

/**
 * Форматируем сообщения по шаблону.
 * Например сообщения в слешах делаются серыми чтобы отличить оффтоп
 * <p>
 * Использован хак для Термоса. Событие вызывается последним, отменяет себя и печатает сообщение.
 */
public class ChatFormat {
    private static final Pattern OFFTOP_PATTERN = Pattern.compile("(^\\\\.*\\\\$)|(^\\\\{2}.*$)");
    private static final EnumChatFormatting OFFTOP_COLOR = EnumChatFormatting.GRAY;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.SERVER)
    public void handleOfftop(ServerChatEvent event) {
        if (OFFTOP_PATTERN.matcher(event.message).matches()) {
            event.component.getChatStyle().setColor(OFFTOP_COLOR);

            // Haaax!
            event.setCanceled(true);
            FMLServerHandler.instance().getServer().getConfigurationManager().sendChatMsg(event.component);
        }
    }
}
