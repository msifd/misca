package msifeed.misca.content;


import msifeed.misca.MiscaThings;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileNotificationRenderer extends TileEntitySpecialRenderer<TileNotification> {

    public void render(TileNotification tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        final Block block = tile.getBlockType();
        final int meta = tile.getBlockMetadata();

        GlStateManager.pushMatrix();
        final float magic = 0.6666667F;

        if (block == MiscaThings.standingNote) {
            GlStateManager.translate(x + 0.5, y + 0.75 * magic, z + 0.5);

            EntityPlayer player = Minecraft.getMinecraft().player;
            final float yaw = MathHelper.wrapDegrees(player.rotationYaw) + 180;
            GlStateManager.rotate(-yaw, 0, 1, 0);
            GlStateManager.rotate(-player.rotationPitch, 1, 0, 0);
        } else {
            final float rotation;
            if (meta == 2) rotation = 180;
            else if (meta == 4) rotation = 90;
            else if (meta == 5) rotation = -90;
            else rotation = 0;

            GlStateManager.translate(x + 0.5, y + 0.75 * magic, z + 0.5);
            GlStateManager.rotate(-rotation, 0, 1, 0);
            GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
        }


        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        final float scale = 0.016666668F * magic;
        GlStateManager.translate(0.0F, 0.5F * magic, 0);
        GlStateManager.scale(scale, -scale, scale);
        GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * scale);
        GlStateManager.depthMask(false);

        if (tile.lineWidth == null) {
            tile.lineWidth = new int[tile.signText.length];
            for (int i = 0; i < tile.signText.length; ++i) {
                final ITextComponent comp = tile.signText[i];
                tile.lineWidth[i] = fontrenderer.getStringWidth(comp.getFormattedText());
            }
        }

        for (int i = 0; i < tile.signText.length; ++i) {
            String s = tile.signText[i].getFormattedText();
            if (i == tile.lineBeingEdited) {
                s = "> " + s + " <";
            }
            fontrenderer.drawString(s, -tile.lineWidth[i] / 2, i * 10 - tile.signText.length * 5, 0);
        }

        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}