package msifeed.misca.chatex.client.format;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class GlobalFormat {
    public static ITextComponent format(EntityPlayer self, String speaker, String msg) {
        return new TextComponentString(msg);
    }
}
