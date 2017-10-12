package msifeed.mc.misca.things.client;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import msifeed.mc.misca.things.RegularChest;

public class RegularChestEntityRenderer extends TileEntitySpecialRenderer {
    private ModelChest model_small = new ModelChest();
    private ModelChest model_large = new ModelLargeChest();

    public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_) {
        renderChestTileEntityAt((RegularChest.ChestEntity) p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }

    public void renderChestTileEntityAt(RegularChest.ChestEntity entity, double xPos, double yPos, double zPos, float p_147500_8_) {
        ResourceLocation texture;
        int block_rotation = 0;

        if (entity.hasWorldObj()) {
            Block block = entity.getBlockType();
            block_rotation = entity.getBlockMetadata();

            if (block instanceof RegularChest && block_rotation == 0) {
                try {
                    ((RegularChest) block).func_149954_e(entity.getWorldObj(), entity.xCoord, entity.yCoord, entity.zCoord);
                } catch (ClassCastException e) {
                    FMLLog.severe("Attempted to render a chest at %d,  %d, %d that was not a chest", entity.xCoord, entity.yCoord, entity.zCoord);
                }
                block_rotation = entity.getBlockMetadata();
            }

            entity.checkForAdjacentChests();
        }

        if (entity.adjacentChestZNeg != null || entity.adjacentChestXNeg != null) {
            return;
        }

        ModelChest modelchest;

        if (entity.adjacentChestXPos == null && entity.adjacentChestZPos == null) {
            modelchest = this.model_small;
            texture = new ResourceLocation("misca:textures/entity/" + entity.getNameBase() + ".png");
        } else {
            modelchest = this.model_large;
            texture = new ResourceLocation("misca:textures/entity/" + entity.getNameBase() + "_large.png");
        }

        this.bindTexture(texture);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) xPos, (float) yPos + 1.0F, (float) zPos + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        short render_rotation = 0;

        switch (block_rotation) {
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

        if (block_rotation == 2 && entity.adjacentChestXPos != null)
            GL11.glTranslatef(1.0F, 0.0F, 0.0F);

        if (block_rotation == 5 && entity.adjacentChestZPos != null)
            GL11.glTranslatef(0.0F, 0.0F, -1.0F);

        GL11.glRotatef((float) render_rotation, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        float f1 = entity.prevLidAngle + (entity.lidAngle - entity.prevLidAngle) * p_147500_8_;
        float f2;

        if (entity.adjacentChestZNeg != null) {
            f2 = entity.adjacentChestZNeg.prevLidAngle + (entity.adjacentChestZNeg.lidAngle - entity.adjacentChestZNeg.prevLidAngle) * p_147500_8_;
            if (f2 > f1) f1 = f2;
        }

        if (entity.adjacentChestXNeg != null) {
            f2 = entity.adjacentChestXNeg.prevLidAngle + (entity.adjacentChestXNeg.lidAngle - entity.adjacentChestXNeg.prevLidAngle) * p_147500_8_;
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