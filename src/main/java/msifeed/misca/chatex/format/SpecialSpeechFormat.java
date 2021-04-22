package msifeed.misca.chatex.format;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class SpecialSpeechFormat {
    public static ITextComponent offtop(EntityPlayerMP sender, String msg) {
        final ITextComponent tc = new TextComponentTranslation("misca.chatex.offtop", sender.getDisplayName(), msg);
        tc.getStyle().setColor(TextFormatting.GRAY);
        return tc;
    }

    public static ITextComponent global(EntityPlayerMP sender, String msg) {
        final ITextComponent tc = new TextComponentTranslation("misca.chatex.global", sender.getDisplayName(), msg);
        tc.getStyle().setColor(TextFormatting.DARK_GREEN);
        return tc;
    }

    public static ITextComponent gmGlobal(EntityPlayerMP sender, String msg) {
        final ITextComponent tc = new TextComponentTranslation("misca.chatex.gmGlobal", sender.getName(), msg);
        tc.getStyle().setColor(TextFormatting.DARK_AQUA);
        return tc;
    }
}
