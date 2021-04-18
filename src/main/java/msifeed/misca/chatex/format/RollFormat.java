package msifeed.misca.chatex.format;

import msifeed.misca.charsheet.CharEffort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class RollFormat {
    public static ITextComponent dice(@Nullable EntityPlayer speaker, String spec, long result) {
        final String name = speaker != null ? speaker.getDisplayNameString() : "???";
        final String text = String.format("[ROLL] %s: %s = %d", name, spec, result);
        final ITextComponent tc = new TextComponentString(text);
        tc.getStyle().setColor(TextFormatting.GOLD);
        return tc;
    }

    public static ITextComponent effort(@Nullable EntityPlayer speaker, CharEffort effort, int amount, int difficulty, boolean result) {
        final String name = speaker != null ? speaker.getDisplayNameString() : "???";
        final String text = String.format("[EFFORT] %s: %s %d/%d = ", name, effort.toString(), amount, difficulty);

        final ITextComponent tc = new TextComponentString(text);
        tc.getStyle().setColor(TextFormatting.GOLD);

        final ITextComponent res = new TextComponentString(result ? "SUCCESS" : "FAIL");
        res.getStyle().setColor(result ? TextFormatting.GREEN : TextFormatting.RED);
        tc.appendSibling(res);

        return tc;
    }
}
