package msifeed.misca;

import msifeed.misca.chatex.ChatexConfig;
import msifeed.misca.environ.EnvironRule;

import java.util.HashMap;
import java.util.Map;

public class MiscaSharedConfig {
    public ChatexConfig chat = new ChatexConfig();
    public Map<Integer, EnvironRule> environ = new HashMap<>();
}
