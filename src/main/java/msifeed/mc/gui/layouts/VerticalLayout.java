package msifeed.mc.gui.layouts;

import com.google.common.collect.Lists;
import msifeed.mc.gui.widgets.IWidget;

import java.util.List;

public class VerticalLayout extends BaseLayout {
    private List<IWidget> children = Lists.newArrayList();

    @Override
    protected void updateLayout() {
        int free_height = getPossibleHeight();
        int padding = 0;
        if (free_height > 0) {
            for (IWidget w : children) free_height -= w.getHeight();
            padding = free_height / (children.size() + 1);
        }
        int occupied_height = 0;
        for (IWidget w : children) {
            occupied_height += padding;
            w.setPosX(getPosX() + (maxWidth - w.getWidth()) / 2);
            w.setPosY(getPosY() + occupied_height);
            occupied_height += w.getHeight();
        }
    }

    @Override
    public List<IWidget> getChildren() {
        return children;
    }
}
