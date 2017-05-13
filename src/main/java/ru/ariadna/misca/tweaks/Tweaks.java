package ru.ariadna.misca.tweaks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tweaks {
    static final TweaksConfig config = new TweaksConfig();
    static Logger logger = LogManager.getLogger("Misca-Tweaks");
    private OfftopFormat offtopFormat = new OfftopFormat();
    private HideNametag hideNametag = new HideNametag();
    private SlowMining slowMining = new SlowMining();
    private SpawnInvincibility spawnInvincibility = new SpawnInvincibility();

    public void preInit() {
        config.init();
    }

    @SideOnly(Side.SERVER)
    public void initServer() {
        MinecraftForge.EVENT_BUS.register(offtopFormat);
        MinecraftForge.EVENT_BUS.register(spawnInvincibility);
    }

    @SideOnly(Side.CLIENT)
    public void initClient() {
        MinecraftForge.EVENT_BUS.register(hideNametag);
    }

    public void initCommon() {
        slowMining.init();
    }
}
