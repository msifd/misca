package msifeed.mc.aorta.genesis.blocks.client;

import cpw.mods.fml.common.FMLLog;
import msifeed.mc.aorta.genesis.blocks.templates.ChestTemplate;
import msifeed.mc.aorta.genesis.blocks.templates.ChestTemplate.ChestEntity;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GenesisChestEntityRenderer extends TileEntitySpecialRenderer {
    private ModelChest model_small = new ModelChest();
    private ModelChest model_large = new ModelLargeChest();

    public void renderTileEntityAt(TileEntity entity, double xPos, double yPos, double zPos, float partialTickTime) {
        if (!entity.hasWorldObj()) {
            FMLLog.severe("Attempted to render a genesis chest with missing block at %d,  %d, %d", entity.xCoord, entity.yCoord, entity.zCoord);
            return;
        }

        if (entity instanceof ChestEntity)
            renderChest(entity.getBlockType(), (ChestEntity) entity, xPos, yPos, zPos,  entity.getBlockMetadata(), partialTickTime);
    }

    void renderChest(Block someBlock, ChestEntity entity, double xPos, double yPos, double zPos, int meta, float partialTickTime) {
        if (!(someBlock instanceof ChestTemplate)) {
            FMLLog.severe("Attempted to render a chest at %d,  %d, %d that was not a chest", entity.xCoord, entity.yCoord, entity.zCoord);
            return;
        }
        final ChestTemplate block = (ChestTemplate) someBlock;

        int blockRotation = meta;
        if (blockRotation == 0) {
            block.func_149954_e(entity.getWorldObj(), entity.xCoord, entity.yCoord, entity.zCoord);
            blockRotation = entity.getBlockMetadata();
        }

        entity.checkForAdjacentChests();
        if (entity.adjacentChestZNeg != null || entity.adjacentChestXNeg != null) {
            return;
        }

        final ModelChest modelchest;
        final ResourceLocation texture;
        final ResourceLocation rawTextureName = new ResourceLocation(block.getTextureName());
        if (entity.adjacentChestXPos == null && entity.adjacentChestZPos == null) {
            modelchest = this.model_small;
            texture = new ResourceLocation(rawTextureName.getResourceDomain(), "textures/entity/" + rawTextureName.getResourcePath() + ".png");
        } else {
            modelchest = this.model_large;
            texture = new ResourceLocation(rawTextureName.getResourceDomain(), "textures/entity/" + rawTextureName.getResourcePath() + "_large.png");
        }

        renderModel(entity, xPos, yPos, zPos, partialTickTime, blockRotation, modelchest, texture);
    }

    private void renderModel(ChestEntity entity, double xPos, double yPos, double zPos, float partialTickTime, int blockRotation, ModelChest modelchest, ResourceLocation texture) {
        this.bindTexture(texture);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) xPos, (float) yPos + 1.0F, (float) zPos + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        short render_rotation = 0;

        switch (blockRotation) {
            case 2:
                render_rotation = 180;
                break;
            case 3:
                render_rotation = 0;
                break;
            case 4:
                render_rotation = 90;
                break;
            case 5:
                render_rotation = -90;
                break;
        }

        if (blockRotation == 2 && entity.adjacentChestXPos != null)
            GL11.glTranslatef(1.0F, 0.0F, 0.0F);

        if (blockRotation == 5 && entity.adjacentChestZPos != null)
            GL11.glTranslatef(0.0F, 0.0F, -1.0F);

        GL11.glRotatef((float) render_rotation, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        float f1 = entity.prevLidAngle + (entity.lidAngle - entity.prevLidAngle) * partialTickTime;
        float f2;

        if (entity.adjacentChestZNeg != null) {
            f2 = entity.adjacentChestZNeg.prevLidAngle + (entity.adjacentChestZNeg.lidAngle - entity.adjacentChestZNeg.prevLidAngle) * partialTickTime;
            if (f2 > f1) f1 = f2;
        }

        if (entity.adjacentChestXNeg != null) {
            f2 = entity.adjacentChestXNeg.prevLidAngle + (entity.adjacentChestXNeg.lidAngle - entity.adjacentChestXNeg.prevLidAngle) * partialTickTime;
            if (f2 > f1) f1 = f2;
        }

        f1 = 1.0F - f1;
        f1 = 1.0F - f1 * f1 * f1;
        modelchest.chestLid.rotateAngleX = -(f1 * (float) Math.PI / 2.0F);
        modelchest.renderAll();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}