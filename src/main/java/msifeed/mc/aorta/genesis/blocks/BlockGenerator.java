package msifeed.mc.aorta.genesis.blocks;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.aorta.genesis.GenesisCreativeTab;
import msifeed.mc.aorta.genesis.Generator;
import msifeed.mc.aorta.genesis.GenesisTrait;
import msifeed.mc.aorta.genesis.blocks.templates.*;
import msifeed.mc.aorta.genesis.blocks.templates.special.SpecialBushTemplate;
import msifeed.mc.aorta.genesis.blocks.templates.special.SpecialLogTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.HashSet;

import static msifeed.mc.aorta.genesis.GenesisTrait.*;

public class BlockGenerator implements Generator {
    @Override
    public void init() {
        GameRegistry.registerTileEntity(ChestTemplate.ChestEntity.class, ChestTemplate.ChestEntity.ID);
        GameRegistry.registerTileEntity(ContainerTemplate.TileEntityContainer.class, "aorta.container");
    }

    @Override
    public void generate(JsonObject json, HashSet<GenesisTrait> traits) {
        final BlockGenesisUnit unit = new BlockGenesisUnit(json, traits);

        final Block block = makeBaseBlock(unit);
        fillCommons(unit, block);
        if (block instanceof BlockTraitCommons.Getter)
            applyBlockType(unit, ((BlockTraitCommons.Getter) block).getCommons());

        if (block instanceof SpecialBlockRegisterer)
            ((SpecialBlockRegisterer) block).register(unit.id);
        else {
            block.setCreativeTab(GenesisCreativeTab.BLOCKS);
            GameRegistry.registerBlock(block, ItemBlockTemplate.class, unit.id);
        }

        if (unit.hasTrait(add_stairs))
            generateStairs(unit, block);
        if (unit.hasTrait(add_slabs))
            generateSlabs(unit, block);
    }

    private void applyBlockType(BlockGenesisUnit unit, BlockTraitCommons commons) {
        if (unit.hasTrait(half)) {
            commons.half = true;
        }

        if (unit.hasTrait(crossed_squares))
            commons.type = BlockTraitCommons.Type.CROSS;
        else if (unit.hasTrait(pillar))
            commons.type = BlockTraitCommons.Type.PILLAR;
        else if (unit.hasTrait(rotatable))
            commons.type = BlockTraitCommons.Type.ROTATABLE;
    }

    private Block makeBaseBlock(BlockGenesisUnit unit) {
        if (unit.hasTrait(container)) {
            final int rows;
            if (unit.hasTrait(large))
                rows = 6;
            else if (unit.hasTrait(small))
                rows = 2;
            else if (unit.hasTrait(tiny))
                rows = 1;
            else
                rows = 3;
            return new ContainerTemplate(unit, getMaterial(unit), rows);
        } else if (unit.hasTrait(chest)) {
            return new ChestTemplate(unit);
        } else if (unit.hasTrait(door)) {
            return new DoorTemplate(unit, getMaterial(unit));
        } else if (unit.hasTrait(torch)) {
            return new TorchTemplate(unit);
        } else if (unit.hasTrait(pane)) {
            return new PaneTemplate(unit, getMaterial(unit));
        } else if (unit.hasTrait(bed)) {
            return new BedTemplate(unit);
        } else if (unit.hasTrait(special_log)) {
            return new SpecialLogTemplate(unit, getMaterial(unit));
        } else if (unit.hasTrait(special_bush)) {
            return new SpecialBushTemplate(unit, getMaterial(unit));
        } else {
            return new BlockTemplate(unit, getMaterial(unit));
        }
    }

    private void generateStairs(BlockGenesisUnit unit, Block parent) {
        final String id = unit.id + "_stairs";
        final Block stairs = new StairsTemplate(parent, id, ((BlockTraitCommons.Getter) parent).getCommons());
        fillCommons(unit, stairs);
        stairs.setCreativeTab(GenesisCreativeTab.BLOCKS);
        GameRegistry.registerBlock(stairs, ItemBlockTemplate.class, id);
    }

    private void generateSlabs(BlockGenesisUnit unit, Block parent) {
        final BlockTraitCommons commons = ((BlockTraitCommons.Getter) parent).getCommons();
        final String singleId = unit.id + "_slab";
        final String doubleId = unit.id + "_doubleslab";
        final SlabTemplate slabSingle = new SlabTemplate(parent, false, singleId, commons);
        final SlabTemplate slabDouble = new SlabTemplate(parent, true, doubleId, commons);

        fillCommons(unit, slabSingle);
        fillCommons(unit, slabDouble);
        slabSingle.setCreativeTab(GenesisCreativeTab.BLOCKS);
        slabDouble.setCreativeTab(null);

        GameRegistry.registerBlock(slabSingle, SlabTemplate.SlabItem.class, singleId, slabSingle, slabDouble);
        GameRegistry.registerBlock(slabDouble, ItemBlockTemplate.class, doubleId);
    }

    private Material getMaterial(BlockGenesisUnit unit) {
        if (unit.hasTrait(wooden))
            return Material.wood;
        else if (unit.hasTrait(stone))
            return Material.rock;
        else if (unit.hasTrait(metal))
            return Material.iron;
        else
            return Material.wood;
    }

    private void fillCommons(BlockGenesisUnit unit, Block block) {
        // Hardness as planks
        block.setHardness(2.0F);
        block.setResistance(5.0F);

        if (unit.hasTrait(unbreakable)) {
            block.setBlockUnbreakable();
            block.setResistance(6000000);
        }

        if (unit.hasTrait(transparent)) {
            block.setLightOpacity(0);
        }

        if (unit.hasTrait(bright_light)) {
            block.setLightLevel(0.9375f);
        } else if (unit.hasTrait(dim_light)) {
            block.setLightLevel(0.5f);
        }

        if (block instanceof BlockTraitCommons.Getter) {
            final BlockTraitCommons traits = ((BlockTraitCommons.Getter) block).getCommons();

            if (unit.hasTrait(transparent))
                traits.transparent = true;
            if (unit.hasTrait(with_alpha))
                traits.useAlphaChannel = true;
            if (unit.hasTrait(not_collidable))
                traits.notCollidable = true;

            if (unit.hasTrait(large))
                traits.size = BlockTraitCommons.Size.LARGE;
            else if (unit.hasTrait(small))
                traits.size = BlockTraitCommons.Size.SMALL;
            else if (unit.hasTrait(tiny))
                traits.size = BlockTraitCommons.Size.TINY;
        }

        if (FMLCommonHandler.instance().getSide().isClient())
            fillTexture(unit, block);
    }

    @SideOnly(Side.CLIENT)
    private void fillTexture(BlockGenesisUnit unit, Block block) {
        if (unit.textureArray != null && unit.textureLayout != null) {
            ((BlockTraitCommons.Getter) block).getCommons().textureLayout = new BlockTextureLayout(unit.textureArray, unit.textureLayout);
        }
        block.setBlockTextureName(unit.textureString);
    }
}
