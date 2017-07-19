package msifeed.mc.gui.widgets;

public abstract class BaseWidget implements IWidget {
    protected int posX, posY, zLevel;
    protected int width, height;
    protected boolean visible = true;
    private IWidgetComposite parent;

    public BaseWidget() {

    }

    public BaseWidget(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public BaseWidget(IWidgetComposite parent, int width, int height) {
        this(width, height);
        setParent(parent);
    }

    public IWidgetComposite getParent() {
        return parent;
    }

    public void setParent(IWidgetComposite parent) {
        if (this == parent) throw new RuntimeException("Cyclic widget parenting!");
        if (this.parent != null) this.parent.removeChild(this);
        this.parent = parent;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
        updateParent();
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
        updateParent();
    }

    public float getZLevel() {
        return zLevel;
    }

    public void setZLevel(int zLevel) {
        this.zLevel = zLevel;
        updateParent();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        updateParent();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        updateParent();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected void updateParent() {
        if (this.parent != null) this.parent.update();
    }
}
