package msifeed.mc.aorta.genesis.items.client;

import msifeed.mc.aorta.genesis.items.IItemTemplate;
import msifeed.mc.aorta.genesis.items.data.ItemRenderData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class ItemCustomRender implements IItemRenderer {
    private boolean renderFlag = false;

    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        return !renderFlag && supportedRenderType(type) && itemHasRenderData(stack.getItem());
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return supportedRenderType(type);
    }

    private boolean supportedRenderType(ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED;
    }

    private boolean itemHasRenderData(Item item) {
        return item instanceof IItemTemplate && ((IItemTemplate) item).getUnit().renderData != null;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        renderFlag = true;

        final Item item = stack.getItem();
        final EntityLivingBase entity = (EntityLivingBase) data[1];

        final ItemRenderData renderData = ((IItemTemplate) item).getUnit().renderData;
        final float scale = renderData.scale + 1.5f;
        final float thickness = renderData.thickness * 0.0625f; // 0.0625f is default
        final float recess = renderData.recess;

        /// align with tip of hand
        GL11.glTranslatef(1.5F, 1.5F, 1.5F);
        GL11.glRotatef(135, 0, 1, 0);
        GL11.glRotatef(-58f, 0, 0, 1);
        GL11.glTranslatef(1.525f, 0.344f, 0);
        GL11.glRotatef(-12f, 0, 0, 1);

        // center item thickness
        GL11.glTranslatef(0, 0, (thickness / 2) * scale);

        GL11.glTranslatef(-2 * renderData.offset, 0, 0); // custom offset from tip of hand
        GL11.glTranslatef(0, recess, 0); // move item out of hand
        GL11.glRotatef(-45 + renderData.rotation, 0, 0, 1); // rotate 45 deg + custom
        GL11.glScalef(scale, scale, scale); // scale item

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (item.requiresMultipleRenderPasses()) {
            for (int i = 0; i < stack.getItem().getRenderPasses(stack.getItemDamage()); ++i)
                renderItem(stack, entity, thickness, i);
        } else {
            renderItem(stack, entity, thickness, 0);
        }

        GL11.glDisable(GL11.GL_BLEND);

        renderFlag = false;
    }

    private void renderItem(ItemStack stack, EntityLivingBase entity, float thickness, int pass) {
        final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        textureManager.bindTexture(textureManager.getResourceLocation(stack.getItemSpriteNumber())); // bind items.png ???
        TextureUtil.func_152777_a(false, false, 1.0F);

        final IIcon i = entity.getItemIcon(stack, pass);
        ItemRenderer.renderItemIn2D(Tessellator.instance,
                i.getMaxU(), i.getMinV(), i.getMinU(), i.getMaxV(),
                i.getIconWidth(), i.getIconHeight(), thickness);
    }

    private void renderDebug() {
        final Tessellator tes = Tessellator.instance;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(3);
        tes.startDrawing(GL11.GL_LINES);
        tes.setColorRGBA_F(1, 0, 0, 1);
        tes.addVertex(0, 0, 0);
        tes.addVertex(1, 0, 0);
        tes.setColorRGBA_F(0, 1, 0, 1);
        tes.addVertex(0, 0, 0);
        tes.addVertex(0, 1, 0);
        tes.setColorRGBA_F(0, 0, 1, 1);
        tes.addVertex(0, 0, 0);
        tes.addVertex(0, 0, 1);
        tes.draw();

        GL11.glEnable(GL11.GL_BLEND);
        tes.startDrawingQuads();
        tes.setColorRGBA_F(1, 0, 0, 0.5f);
        tes.addVertex(0, 0, 0);
        tes.addVertex(0, 0, -0.5);
        tes.addVertex(1, 0, -0.5);
        tes.addVertex(1, 0, 0);
        tes.draw();
        tes.startDrawingQuads();
        tes.setColorRGBA_F(0, 0, 1, 0.5f);
        tes.addVertex(0, 0, 0);
        tes.addVertex(-1.5, 0, 0);
        tes.addVertex(-1.5, 0, 1);
        tes.addVertex(0, 0, 1);
        tes.draw();
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}