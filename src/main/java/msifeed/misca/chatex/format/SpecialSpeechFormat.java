package msifeed.misca.chatex.format;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class SpecialSpeechFormat {
    public static ITextComponent offtop(EntityPlayer speaker, String msg) {
        final String name = speaker != null ? speaker.getDisplayNameString() : "???";
        final ITextComponent tc = new TextComponentString("[OFF] " + name + ": " + msg);
        tc.getStyle().setColor(TextFormatting.GRAY);
        return tc;
    }

    public static ITextComponent global(EntityPlayer self, String speaker, String msg) {
        final ITextComponent tc = new TextComponentString("[GLOB] " + speaker + ": " + msg);
        tc.getStyle().setColor(TextFormatting.DARK_GREEN);
        return tc;
    }

    public static ITextComponent gmGlobal(EntityPlayer self, String speaker, String msg) {
        final ITextComponent tc = new TextComponentString("[GMGL] " + speaker + ": " + msg);
        tc.getStyle().setColor(TextFormatting.DARK_AQUA);
        return tc;
    }
}
