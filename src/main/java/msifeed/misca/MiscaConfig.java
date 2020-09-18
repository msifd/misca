package msifeed.misca;

import msifeed.misca.chatex.ChatexConfig;
import msifeed.misca.genesis.GenesisConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = Misca.MODID)
public class MiscaConfig {
    public static GenesisConfig genesis = new GenesisConfig();
    public static ChatexConfig chat = new ChatexConfig();
}
