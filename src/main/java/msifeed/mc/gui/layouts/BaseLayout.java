package msifeed.mc.gui.layouts;

import msifeed.mc.gui.widgets.BaseWidget;
import msifeed.mc.gui.widgets.IWidget;
import msifeed.mc.gui.widgets.IWidgetComposite;
import net.minecraft.client.Minecraft;

public abstract class BaseLayout extends BaseWidget implements IWidgetComposite {
    protected int maxWidth, maxHeight;
    private boolean updating = false;

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
        return parent != null ? parent.getPossibleHeight() : getWidth();
    }

    @Override
    public void bindChild(IWidget child) {
        IWidgetComposite.super.bindChild(child);

        maxWidth = maxHeight = 0;
        for (IWidget w : getChildren()) {
            maxWidth = Math.max(maxWidth, w.getWidth());
            maxHeight = Math.max(maxHeight, w.getHeight());
        }
    }
}
