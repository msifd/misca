package msifeed.mc.gui.layouts;

import msifeed.mc.gui.widgets.IWidget;

import java.util.Collections;
import java.util.List;

public class CenterLayout extends BaseLayout {
    private IWidget child;

    public CenterLayout(IWidget centeredWidget) {
        bindChild(centeredWidget);
    }

    @Override
    protected void updateLayout() {
        IWidget parent = getParent();
        if (child == null || parent == null) return;
        child.setPosX(parent.getWidth() / 2 - child.getWidth() / 2);
        child.setPosY(parent.getHeight() / 2 - child.getHeight() / 2);
    }

    @Override
    public List<IWidget> getChildren() {
        return Collections.singletonList(this.child);
    }

    @Override
    public void addChild(IWidget child) {
        this.child = child;
    }

    @Override
    public void removeChild(IWidget child) {
        if (this.child == child) this.child = null;
    }

    public IWidget getCenteredWidget() {
        return child;
    }

    public void setCenteredWidget(IWidget centeredWidget) {
        this.child = centeredWidget;
    }
}
