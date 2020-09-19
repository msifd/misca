package msifeed.misca.genesis;

import msifeed.misca.MiscaConfig;
import msifeed.misca.genesis.blocks.BlockRule;
import msifeed.misca.genesis.items.ItemRule;
import msifeed.misca.genesis.rules.IGenesisRule;
import msifeed.misca.genesis.rules.RuleLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class Genesis {
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        load(BlockRule.class, MiscaConfig.genesis.blocksDir);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        load(ItemRule.class, MiscaConfig.genesis.itemsDir);
    }

    private void load(Class<? extends IGenesisRule> ruleType, String subfolder) {
        final File genesisDir = new File(Loader.instance().getConfigDir(), MiscaConfig.genesis.genesisDir);
        final File subDir = new File(genesisDir, subfolder);
        if (!subDir.exists()) {
            subDir.mkdirs();
        }

        final RuleLoader loader = new RuleLoader(ruleType);
        loader.load(subDir);
    }
}
