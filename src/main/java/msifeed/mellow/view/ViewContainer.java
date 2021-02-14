package msifeed.mellow.view;

import msifeed.mellow.utils.Geom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ViewContainer extends View {
    protected List<View> viewList = new ArrayList<>();

    public void addView(View view) {
        viewList.add(view);
    }

    public void clearViews() {
        viewList.clear();
    }

    public List<View> getViewList() {
        return viewList;
    }

    public Stream<View> getViewsAtPoint(int x, int y) {
        return viewList.stream()
                .filter(view -> view.getRenderGeom().contains(x, y))
                .sorted(Comparator.comparingInt(v -> v.getRenderGeom().z));
    }

    @Override
    public void render(Geom geom) {
        for (View v : viewList)
            v.render(v.getRenderGeom());
    }
}
