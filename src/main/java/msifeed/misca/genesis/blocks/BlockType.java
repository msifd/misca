package msifeed.misca.genesis.blocks;

import msifeed.misca.genesis.blocks.templates.BlockContainerTemplate;
import msifeed.misca.genesis.blocks.templates.BlockPillarTemplate;
import msifeed.misca.genesis.blocks.templates.BlockTemplate;
import net.minecraft.block.Block;

import java.util.function.Function;

public enum BlockType {
    plain(BlockTemplate::new),
    pillar(BlockPillarTemplate::new),
    container(BlockContainerTemplate::new);

    private final Function<BlockRule, Block> factory;

    BlockType(Function<BlockRule, Block> factory) {
        this.factory = factory;
    }

    Block createBlock(BlockRule rule) {
        return factory.apply(rule);
    }
}
