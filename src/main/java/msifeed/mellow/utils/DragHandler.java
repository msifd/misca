package msifeed.mellow.utils;

public class DragHandler {
    protected boolean dragging = false;
    protected Point drag = new Point();
    protected Point current = new Point();

    public void translateDrag(Geom geom) {
        geom.translate(drag.x, drag.y, 0);
    }

    public void startDrag(int x, int y) {
        dragging = true;
        current.set(x, y);
    }

    public void drag(int x, int y) {
        if (dragging) {
            drag.translate(x - current.x, y - current.y);
            current.set(x, y);
        }
    }

    public void stopDrag() {
        dragging = false;
        current.set(0, 0);
    }

    public void reset() {
        drag.set(0, 0);
        current.set(0, 0);
    }
}
