package msifeed.mc.aorta.genesis.blocks.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import msifeed.mc.aorta.genesis.blocks.BlockTraitCommons;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class GenesisBlockRenderer implements ISimpleBlockRenderingHandler {
    public static final int ROTATABLE = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        renderBlockItem(block, metadata, renderer);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return renderRotatable(world, x, y, z, block, renderer);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ROTATABLE;
    }

    private void renderBlockItem(Block block, int meta, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    private boolean renderRotatable(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer) {
        final int meta = world.getBlockMetadata(x, y, z);
        final int ort = BlockTraitCommons.getRotatedOrt(meta);

        if (ort > 3) {
            renderer.uvRotateTop = ort - 3;
            renderer.uvRotateBottom = ort - 3;
        }
        else if (ort > 1) {
            renderer.uvRotateTop = ort + 1;
            renderer.uvRotateBottom = 5 - ort + 1;
        }

        if (ort == 0)
            renderer.uvRotateEast = 3;
        if (ort == 1)
            renderer.uvRotateEast = 3;

        final boolean result = renderer.renderStandardBlock(block, x, y, z);

        renderer.uvRotateTop = 0;
        renderer.uvRotateBottom = 0;
        renderer.uvRotateEast = 0;

        return result;
    }
}
