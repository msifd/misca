package msifeed.misca.genesis;

import msifeed.misca.genesis.blocks.BlockRule;
import msifeed.misca.genesis.blocks.tiles.TileEntityContainer;
import msifeed.misca.genesis.items.ItemRule;
import msifeed.misca.genesis.rules.IGenesisRule;
import msifeed.misca.genesis.rules.RuleLoader;
import msifeed.misca.genesis.tabs.CreativeTabRule;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Genesis {
    private Path genesisDir;

    public void preInit() {
        genesisDir = Loader.instance().getConfigDir().toPath().resolve("genesis");
        MinecraftForge.EVENT_BUS.register(this);

        if (FMLCommonHandler.instance().getSide().isClient()) {
            registerTabs();
        }
    }

    public void registerTabs() {
        final RuleLoader loader = new RuleLoader(CreativeTabRule.class);
        loader.loadFile(genesisDir.resolve("tabs.json"));
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity(TileEntityContainer.class, TileEntityContainer.RESOURCE);

        loadDir(BlockRule.class, genesisDir.resolve("blocks"));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        loadDir(ItemRule.class, genesisDir.resolve("items"));
    }

    private void loadDir(Class<? extends IGenesisRule> ruleType, Path dir) {
        if (!Files.isDirectory(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        final RuleLoader loader = new RuleLoader(ruleType);
        loader.loadDir(dir);
    }
}
