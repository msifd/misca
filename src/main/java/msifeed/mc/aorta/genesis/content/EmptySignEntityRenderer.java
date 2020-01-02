package msifeed.mc.aorta.genesis.content;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class EmptySignEntityRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity someTileEntity, double x, double y, double z, float partialTickTime) {
        final Block block = someTileEntity.blockType;
        final int meta = someTileEntity.getBlockMetadata();

        GL11.glPushMatrix();
        float f1 = 0.6666667F;
        float f3;

        if (block == EmptySignBlock.standing_empty_sign) {
            GL11.glTranslated(x + 0.5, y + 0.75 * f1, z + 0.5);

            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            final float yaw = MathHelper.wrapAngleTo180_float(player.rotationYaw) + 180;
            GL11.glRotatef(-yaw, 0, 1, 0);
            GL11.glRotatef(-player.rotationPitch, 1, 0, 0);
        } else {
            f3 = 0.0F;
            if (meta == 2)
                f3 = 180.0F;
            else if (meta == 4)
                f3 = 90.0F;
            else if (meta == 5) {
                f3 = -90.0F;
            }

            GL11.glTranslated(x + 0.5, y + 0.75 * f1, z + 0.5);
            GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
        }


        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        f3 = 0.016666668F * f1;
        GL11.glTranslatef(0.0F, 0.5F * f1, 0);
        GL11.glScalef(f3, -f3, f3);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
        GL11.glDepthMask(false);
        byte b0 = 0;

        final EmptySignBlock.Tile tileEntity = (EmptySignBlock.Tile) someTileEntity;

        for (int i = 0; i < tileEntity.signText.length; ++i) {
            String s = tileEntity.signText[i];

            if (i == tileEntity.lineBeingEdited) {
                s = "> " + s + " <";
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - tileEntity.signText.length * 5, b0);
            } else {
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - tileEntity.signText.length * 5, b0);
            }
        }

        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}