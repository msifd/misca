package msifeed.mc.aorta.genesis.blocks.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import msifeed.mc.aorta.genesis.blocks.templates.ChestTemplate;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GenesisChestRenderer implements ISimpleBlockRenderingHandler {
    public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
    private final ChestTemplate.ChestEntity chestTileEntity = new ChestTemplate.ChestEntity();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderBlocks) {
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        final TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(chestTileEntity);
        if (renderer instanceof GenesisChestEntityRenderer) {
            try {
                ((GenesisChestEntityRenderer) renderer).renderChest(block, chestTileEntity,0.0D, 0.0D, 0.0D, 1, 0.0F);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
                throw new ReportedException(crashreport);
            }
        }

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return RENDER_ID;
    }
}
