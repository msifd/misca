package msifeed.mc.gui.layouts;

import com.google.common.collect.Lists;
import msifeed.mc.gui.widgets.IWidget;

import java.util.List;

public class HorizontalLayout extends BaseLayout {
    private List<IWidget> children = Lists.newArrayList();

    @Override
    protected void updateLayout() {
        int free_width = getPossibleWidth();
        int padding = 0;
        if (free_width > 0) {
            for (IWidget w : children) free_width -= w.getWidth();
            padding = free_width / (children.size() + 1);
        }
        int occupied_width = 0;
        for (IWidget w : children) {
            occupied_width += padding;
            w.setPosX(getPosX() + occupied_width);
            w.setPosY(getPosY() + (maxHeight - w.getHeight()) / 2);
            occupied_width += w.getHeight();
        }
    }

    @Override
    public List<IWidget> getChildren() {
        return children;
    }
}
