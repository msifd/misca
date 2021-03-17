package msifeed.misca.chatex.client.format;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class GlobalFormat {
    public static ITextComponent regular(EntityPlayer self, String speaker, String msg) {
        final ITextComponent tc = new TextComponentString("[GLOB] " + speaker + ": " + msg);
        tc.getStyle().setColor(TextFormatting.DARK_GREEN);
        return tc;
    }

    public static ITextComponent gameMaster(EntityPlayer self, String speaker, String msg) {
        final ITextComponent tc = new TextComponentString("[GMGL] " + speaker + ": " + msg);
        tc.getStyle().setColor(TextFormatting.DARK_AQUA);
        return tc;
    }
}
