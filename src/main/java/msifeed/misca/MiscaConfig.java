package msifeed.misca;

import msifeed.misca.chatex.ChatexConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = Misca.MODID)
public class MiscaConfig {
    public static ChatexConfig chat = new ChatexConfig();
}
