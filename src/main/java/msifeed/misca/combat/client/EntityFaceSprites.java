package msifeed.misca.combat.client;

import msifeed.mellow.sprite.FlatSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public enum EntityFaceSprites {
    INSTANCE;

    private final Map<Class<?>, FlatSprite> entityFaces = new HashMap<>();
    private Method getTextureMethod;

    EntityFaceSprites() {
        try {
            getTextureMethod = Render.class.getDeclaredMethod("getEntityTexture", Entity.class);
            getTextureMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FlatSprite getFaceSprite(Entity entity) {
        if (entity instanceof AbstractClientPlayer)
            return createPlayerSprite((AbstractClientPlayer) entity);
        else
            return entityFaces.computeIfAbsent(entity.getClass(), c -> createEntitySprite(entity));
    }

    private FlatSprite createPlayerSprite(AbstractClientPlayer player) {
        final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        return new FlatSprite(textureManager.getTexture(player.getLocationSkin()),
                64, 64, 8, 8, 8, 8);
    }

    private FlatSprite createEntitySprite(Entity entity) {
        // TODO: return some default sprite
        final RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        final Render<? extends Entity> someRender = renderManager.getEntityRenderObject(entity);
        if (!(someRender instanceof RenderLivingBase)) return null;

        final RenderLivingBase<?> render = (RenderLivingBase<?>) someRender;
        final ModelBase model = render.getMainModel();
        if (model.boxList.isEmpty()) return null;

        final ModelRenderer box = model.boxList.get(0);
        if (box.cubeList.isEmpty()) return null;
        final ModelBox cube = box.cubeList.get(0);

        final ResourceLocation loc = getTexture(render, entity);
        if (loc == null) return null;
        final ITextureObject tex = Minecraft.getMinecraft().getTextureManager().getTexture(loc);

        final double cubeWidth = cube.posX2 - cube.posX1;
        final double cubeHeight = cube.posY2 - cube.posY1;
        return new FlatSprite(tex, box.textureWidth, box.textureHeight,
                box.textureOffsetX + cubeWidth, box.textureOffsetY + cubeHeight, cubeWidth, cubeHeight);
    }

    @Nullable
    private ResourceLocation getTexture(RenderLivingBase<? extends Entity> render, Entity entity) {
        try {
            return (ResourceLocation) getTextureMethod.invoke(render, entity);
        } catch (Exception e) {
            return null;
        }
    }
}
