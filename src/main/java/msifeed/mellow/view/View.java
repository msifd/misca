package msifeed.mellow.view;

import msifeed.mellow.FocusState;
import msifeed.mellow.utils.Geom;

public abstract class View {
    protected final Geom geometry = new Geom();

    public abstract void render();

    public Geom getBaseGeom() {
        return geometry;
    }

    public Geom getRenderGeom() {
        return geometry.clone();
    }

    public final void translate(int x, int y, int z) {
        setPos(geometry.x + x, geometry.y + y, geometry.z + z);
    }

    public void setPos(int x, int y, int z) {
        geometry.setPos(x, y, z);
    }

    public void setSize(int w, int h) {
        geometry.setSize(w, h);
    }

    public boolean isFocused() {
        return FocusState.INSTANCE.isFocused(this);
    }

    public void focus() {
        FocusState.INSTANCE.setFocus(this);
    }
}
