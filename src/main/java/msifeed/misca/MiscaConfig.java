package msifeed.misca;

import msifeed.misca.chatex.ChatexConfig;
import msifeed.misca.environ.EnvironRule;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

@Config(modid = Misca.MODID)
public class MiscaConfig {
    public static ChatexConfig chat = new ChatexConfig();
    public static Map<String, EnvironRule> environ = new HashMap<>();

    public static String windowTitle = "Ariadna";
}
