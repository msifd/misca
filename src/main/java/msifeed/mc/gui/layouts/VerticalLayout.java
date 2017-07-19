package msifeed.mc.gui.layouts;

import com.google.common.collect.Lists;
import msifeed.mc.gui.widgets.IWidget;

import java.util.List;

public class VerticalLayout extends BaseLayout {
    protected int padding = 0;
    private List<IWidget> children = Lists.newArrayList();

    @Override
    protected void updateLayout() {
        int free_height = getPossibleHeight();
        int padding = 0;
        if (free_height > 0) {
            for (IWidget w : children) free_height -= w.getHeight();
            padding = free_height / (children.size() + 1);
        }
        int offset = 0;
        for (IWidget w : children) {
            offset += padding;
            w.setPosX((maxWidth - w.getWidth()) / 2);
            w.setPosY(offset);
            offset += w.getHeight();
        }
    }

    @Override
    public List<IWidget> getChildren() {
        return children;
    }
}
