package msifeed.mc.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GraphicsHelper {
    public static void drawColoredBox(Color color, int x, int y, int z, int width, int height) {
        float[] cmp = color.getRGBComponents(null);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(cmp[0], cmp[1], cmp[2], 0.5f);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(x, y + height, z);
        tessellator.addVertex(x + width, y + height, z);
        tessellator.addVertex(x + width, y, z);
        tessellator.addVertex(x, y, z);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
