package msifeed.misca.names;

import net.minecraftforge.common.MinecraftForge;

public class NamesExtension {
    public void init() {
        MinecraftForge.EVENT_BUS.register(new NametagRender());
    }
}
