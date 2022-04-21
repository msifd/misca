package msifeed.misca.chatex.format;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class RollFormat {
    public static ITextComponent dice(EntityPlayer sender, String spec, long result) {
        final ITextComponent tc = new TextComponentTranslation("misca.chatex.dice", sender.getDisplayName(), spec, result);
        tc.getStyle().setColor(TextFormatting.GOLD);
        return tc;
    }
}
