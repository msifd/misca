package msifeed.misca.chatex.client.gui;

import msifeed.mellow.FocusState;
import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.utils.DragHandler;
import msifeed.mellow.utils.Geom;
import msifeed.mellow.utils.Point;
import msifeed.mellow.view.InputHandler;
import msifeed.mellow.view.View;
import msifeed.misca.MiscaConfig;
import msifeed.misca.client.ClientConfig;
import net.minecraft.util.math.MathHelper;

public class ResizeHandle extends View implements InputHandler.Mouse {
    private final DragHandler dragHandler = new DragHandler();
    private final Point screenSize = new Point();
    private final int xOffset, yOffset;

    public ResizeHandle() {
        this.xOffset = 1;
        this.yOffset = 35;

        setSize(5, 5);
//        MiscaConfig.chatSize.set(350, 180);
    }

    public Point getScreenSize() {
        return screenSize;
    }

    public void resetPos() {
        setPos(MiscaConfig.client.chatSize.x + this.geometry.w + xOffset, screenSize.y - MiscaConfig.client.chatSize.y - yOffset, 10);
    }

    @Override
    public Geom getRenderGeom() {
        final Geom geom = super.getRenderGeom();
        dragHandler.translateDrag(geom);
        geom.y -= geom.y % 9 + 3;
        geom.x = MathHelper.clamp(geom.x, 10, screenSize.x - 15);
        geom.y = MathHelper.clamp(geom.y, 15, screenSize.y - 54);
        return geom;
    }

    @Override
    public void render(Geom geom) {
        RenderShapes.rect(geom, 0xbbffbbbb);
    }

    @Override
    public void onMousePress(int mouseX, int mouseY, int button) {
        dragHandler.startDrag(mouseX, mouseY);
    }

    @Override
    public void onMouseMove(int mouseX, int mouseY, int button) {
        dragHandler.drag(mouseX, mouseY);

        final Geom geom = getRenderGeom();
        MiscaConfig.client.chatSize.set(geom.x - geom.w - xOffset, screenSize.y - geom.y - geom.h - yOffset);
    }

    @Override
    public void onMouseRelease(int mouseX, int mouseY, int button) {
        dragHandler.stopDrag();
        dragHandler.translateDrag(this.geometry);
        dragHandler.reset();
        FocusState.INSTANCE.clearFocus();
    }
}
