package msifeed.mc.gui.layout;

import com.google.common.collect.Lists;
import msifeed.mc.gui.widget.IWidget;

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
