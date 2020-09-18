package msifeed.misca.genesis;

import msifeed.misca.MiscaConfig;
import msifeed.misca.genesis.generator.block.BlockRule;
import msifeed.misca.genesis.rules.RuleLoader;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.IOException;

public class Genesis {
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        final File genesisDir = new File(Loader.instance().getConfigDir(), MiscaConfig.genesis.genesisDir);
        if (!genesisDir.exists())
            genesisDir.mkdirs();

        try {
            final RuleLoader<BlockRule> loader = new RuleLoader<>(BlockRule.class);
            loader.load(new File(genesisDir, MiscaConfig.genesis.blocksDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
