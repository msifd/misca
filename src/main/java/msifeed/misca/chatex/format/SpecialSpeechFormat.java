package msifeed.misca.chatex.format;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class SpecialSpeechFormat {
    public static ITextComponent offtop(ITextComponent name, String msg) {
        final ITextComponent tc = new TextComponentTranslation("misca.chatex.offtop", name, msg);
        tc.getStyle().setColor(TextFormatting.GRAY);
        return tc;
    }

    public static ITextComponent global(String name, String msg) {
        final ITextComponent tc = new TextComponentTranslation("misca.chatex.global", name, msg);
        tc.getStyle().setColor(TextFormatting.DARK_GREEN);
        return tc;
    }

    public static ITextComponent gmGlobal(String name, String msg) {
        final ITextComponent tc = new TextComponentTranslation("misca.chatex.gmGlobal", name, msg);
        tc.getStyle().setColor(TextFormatting.DARK_AQUA);
        return tc;
    }
}
