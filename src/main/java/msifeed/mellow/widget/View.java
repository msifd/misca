package msifeed.mellow.widget;

import msifeed.mellow.utils.Geom;

public abstract class View {
    protected final Geom geometry = new Geom();

    public Geom getGeom() {
        return geometry;
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

    public abstract void render();
}
