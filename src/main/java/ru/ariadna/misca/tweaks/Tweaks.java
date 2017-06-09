package ru.ariadna.misca.tweaks;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
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
    private MiningNerf miningNerf = new MiningNerf();
    private SpawnInvincibility spawnInvincibility = new SpawnInvincibility();
    private DisableSomeCraftingTables disableSomeCraftingTables = new DisableSomeCraftingTables();

    public void preInit() {
        config.load();
        miningNerf.preInit();
    }

    @SideOnly(Side.SERVER)
    public void initServer() {
        MinecraftForge.EVENT_BUS.register(offtopFormat);
        MinecraftForge.EVENT_BUS.register(spawnInvincibility);
    }

    @SideOnly(Side.CLIENT)
    public void initClient() {
        MinecraftForge.EVENT_BUS.register(hideNametag);
        MinecraftForge.EVENT_BUS.register(disableSomeCraftingTables);
    }

    public void initCommon() {

    }

    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(miningNerf.commandStamina);
    }
}
