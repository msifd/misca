package msifeed.mc.gui.layout;

import msifeed.mc.gui.widget.BaseWidget;
import msifeed.mc.gui.widget.IWidget;
import msifeed.mc.gui.widget.IWidgetComposite;
import net.minecraft.client.Minecraft;

public abstract class BaseLayout extends BaseWidget implements IWidgetComposite {
    protected int maxWidth, maxHeight;
    protected int sumWidth, sumHeight;
    private boolean updating = false;

    public BaseLayout bind(IWidget... children) {
        updating = true;
        for (IWidget w : children) bindChild(w);
        updating = false;
        update();
        return this;
    }

    protected abstract void updateLayout();

    @Override
    public void update() {
        if (updating) return;

        updating = true;
        updateLayout();
        for (IWidget w : getChildren()) w.update();
        updating = false;
    }

    @Override
    public void render(Minecraft mc, int mouseX, int mouseY, float tick) {
        for (IWidget w : getChildren()) w.render(mc, mouseX, mouseY, tick);
    }

    @Override
    public int getWidth() {
        return maxWidth;
    }

    @Override
    public int getHeight() {
        return maxHeight;
    }

    @Override
    public int getPossibleWidth() {
        final IWidgetComposite parent = getParent();
        return parent != null ? parent.getPossibleWidth() : getWidth();
    }

    @Override
    public int getPossibleHeight() {
        final IWidgetComposite parent = getParent();
        return parent != null ? parent.getPossibleHeight() : getHeight();
    }

    @Override
    public void bindChild(IWidget child) {
        IWidgetComposite.super.bindChild(child);

        maxWidth = maxHeight = sumWidth = sumHeight = 0;
        for (IWidget w : getChildren()) {
            int wid = w.getWidth(), hei = w.getHeight();
            maxWidth = Math.max(maxWidth, wid);
            maxHeight = Math.max(maxHeight, hei);
            sumWidth += wid;
            sumHeight += hei;
        }
    }
}
