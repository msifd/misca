package msifeed.mc.gui.nim;

public abstract class NimPart {
    private static NimPart focus = null;

    protected int posX, posY, posZ;
    protected int width, height;

    public static boolean focused() {
        return focus != null;
    }

    public static void releaseFocus() {
        focus = null;
    }

    public int getX() {
        return posX;
    }

    public void setX(int posX) {
        this.posX = posX;
    }

    public int getY() {
        return posY;
    }

    public void setY(int posY) {
        this.posY = posY;
    }

    public int getZ() {
        return posZ;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void locate(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean inFocus() {
        return this == focus;
    }

    public void takeFocus() {
        focus = this;
    }

    public abstract void render(int x, int y);
}
