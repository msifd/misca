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
    public void render(Geom geom) {
        if (face != null) face.render(geom.add(faceGeom));
        else RenderShapes.rect(geom.add(faceGeom), 0xffffffff);

        CombatTheme.combatantFrame.render(geom);

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        fr.drawString(String.valueOf(entityId), geom.x, geom.y, 0xffffffff);
    }
}
