package msifeed.misca.chatex;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public interface IChatexProxy {
    void init();
    void registerCommands(FMLServerStartingEvent event);
}
