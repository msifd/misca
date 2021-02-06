package msifeed.misca.combat.client;

import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.sprite.FlatSprite;
import msifeed.mellow.utils.Geom;
import msifeed.mellow.view.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.EntityLivingBase;

public class CombatantFrameView extends View {
    private final Geom faceGeom = new Geom(1, 3, 24, 24);
    private FlatSprite face;
    private int entityId;

    CombatantFrameView() {
        setPos(0, 0, 0);
        setSize(26, 31);
    }

    void setFace(EntityLivingBase entity) {
        this.face = EntityFaceSprites.INSTANCE.getFaceSprite(entity);
        this.entityId = entity.getEntityId();
    }

    @Override
    public void setPos(int x, int y, int z) {
        super.setPos(x, y, z);
        faceGeom.setPos(x + 1, y + 3, z);
    }

    @Override
    public void render() {
        if (face != null) face.render(faceGeom);
        else RenderShapes.rect(faceGeom, 0xffffffff);

        CombatTheme.combatantFrame.render(geometry);

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        fr.drawString(String.valueOf(entityId), geometry.x, geometry.y, 0xffffffff);
    }
}
