package msifeed.mellow.view;

import java.util.ArrayList;
import java.util.List;

public class ViewContainer extends View {
    protected List<View> viewList = new ArrayList<>();

    public void addView(View view) {
        viewList.add(view);
    }

    public List<View> getViewList() {
        return viewList;
    }

    @Override
    public void render() {
        for (View v : viewList)
            v.render();
    }
}
