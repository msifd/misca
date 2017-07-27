package msifeed.mc.gui.layouts;

import com.google.common.collect.Lists;
import msifeed.mc.gui.widgets.IWidget;

import java.util.List;

public class EmptyLayout extends BaseLayout {
    private List<IWidget> children = Lists.newArrayList();

    @Override
    public List<IWidget> getChildren() {
        return children;
    }

    @Override
    protected void updateLayout() {
    }
}
