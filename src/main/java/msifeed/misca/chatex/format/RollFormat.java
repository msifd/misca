package msifeed.misca.chatex.format;

import msifeed.misca.charsheet.CharEffort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class RollFormat {
    public static ITextComponent dice(EntityPlayer sender, String spec, long result) {
        final ITextComponent tc = new TextComponentTranslation("misca.chatex.dice", sender.getDisplayName(), spec, result);
        tc.getStyle().setColor(TextFormatting.GOLD);
        return tc;
    }

    public static ITextComponent effort(EntityPlayer sender, CharEffort effort, int amount, int difficulty, boolean result) {
        final ITextComponent res = new TextComponentString(result ? "SUCCESS" : "FAIL");
        res.getStyle().setColor(result ? TextFormatting.GREEN : TextFormatting.RED);

        final ITextComponent tc = new TextComponentTranslation("misca.chatex.effort", sender.getDisplayName(), effort.tr(), amount, difficulty, res);
        tc.getStyle().setColor(TextFormatting.GOLD);

        return tc;
    }
}
