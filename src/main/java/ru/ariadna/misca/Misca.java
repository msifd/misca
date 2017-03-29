package ru.ariadna.misca;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import ru.ariadna.misca.chat.ChatFormat;

@Mod(modid = Misca.MODID, version = Misca.VERSION, acceptableRemoteVersions = "*")
public class Misca {
    static final String MODID = "Misca";
    static final String VERSION = "0.1";

    private ChatFormat chatFormat = new ChatFormat();

    @EventHandler
    public void init(FMLInitializationEvent event) {
        chatFormat.init();
    }
}
