package msifeed.mellow.utils;

import msifeed.mellow.view.View;
import msifeed.mellow.view.ViewContainer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class UiBuilder {
    private final ViewContainer rootContainer;
    private final Stack<Group> groups = new Stack<>();

    public static UiBuilder of(ViewContainer container) {
        return new UiBuilder(container);
    }

    private UiBuilder(ViewContainer container) {
        this.rootContainer = container;
        this.groups.add(new Group(container, container, container, null));
    }

    public void build() {
        rootContainer.clearViews();
        getGroup().end();
    }

    // Grouping

    public UiBuilder beginGroup() {
        final Group curr = getGroup();
        groups.add(new Group(curr.container, null, curr.currView, curr.prevView));
        return this;
    }

    public UiBuilder endGroup() {
        final Group pop = groups.pop();
        final Group group = getGroup();
        group.prevView = group.currView;
        group.currView = pop.baseView != null ? pop.baseView : pop.currView;
        group.views.addAll(pop.views);

        return this;
    }

    public UiBuilder groupBase() {
        final Group group = getGroup();
        group.baseView = group.currView;
        return this;
    }

    // Adding

    public UiBuilder add(@Nonnull View view) {
        getGroup().add(view);
        return this;
    }

    public UiBuilder add(Supplier<View> supplier) {
        return add(supplier.get());
    }

    public <T> UiBuilder forEach(Iterable<T> iterable, BiConsumer<UiBuilder, T> consumer) {
        for (T val : iterable)
            consumer.accept(this, val);
        return this;
    }

    public <T> UiBuilder forEach(T[] array, BiConsumer<UiBuilder, T> consumer) {
        for (T val : array)
            consumer.accept(this, val);
        return this;
    }

    public <T> UiBuilder forEach(Stream<T> stream, BiConsumer<UiBuilder, T> consumer) {
        stream.forEach(val -> consumer.accept(this, val));
        return this;
    }

    public UiBuilder run(Consumer<UiBuilder> runnable) {
        runnable.accept(this);
        return this;
    }

    // Posing

    public UiBuilder center(Direction direction) {
        final Group group = getGroup();
        final Geom cont = group.container.getBaseGeom();
        final Geom cg = group.currView.getBaseGeom();
        if (direction.isHorizontal()) cg.x = (cont.w - cg.w) / 2;
        if (direction.isVertical()) cg.y = (cont.h - cg.h) / 2;
        return this;
    }

    public UiBuilder below() {
        final Group group = getGroup();
        final Geom pg = group.prevView.getBaseGeom();
        group.currView.setPos(pg.x, pg.y + pg.h, pg.z);
        return this;
    }

    public UiBuilder right() {
        final Group group = getGroup();
        final Geom pg = group.prevView.getBaseGeom();
        group.currView.setPos(pg.x + pg.w + 1, pg.y, pg.z);
        return this;
    }

    public UiBuilder at(int x, int y) {
        final Group group = getGroup();
        group.currView.setPos(x, y, group.currView.getBaseGeom().z);
        return this;
    }

    public UiBuilder at(int x, int y, int z) {
        getGroup().currView.setPos(x, y, z);
        return this;
    }

    public UiBuilder move(int x, int y, int z) {
        getGroup().currView.translate(x, y, z);
        return this;
    }

    // Group posing

    public UiBuilder centerGroup(Direction direction) {
        final Group group = getGroup();
        final Geom cont = group.container.getBaseGeom();

        final Geom cg = new Geom(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
        for (View view : group.views) {
            final Geom g = view.getBaseGeom();
            cg.x = Math.min(cg.x, g.x);
            cg.y = Math.min(cg.y, g.y);
            cg.w = Math.max(cg.w, g.x + g.w);
            cg.h = Math.max(cg.h, g.y + g.h);
        }
        cg.w -= cg.x;
        cg.h -= cg.y;

        if (direction.isHorizontal()) {
            final int diff = cg.x - (cont.w - cg.w) / 2;
            for (View view : group.views)
                view.getBaseGeom().translate(-diff, 0);
        }
        if (direction.isVertical()) {
            final int diff = cg.y - (cont.h - cg.h) / 2;
            for (View view : group.views)
                view.getBaseGeom().translate(0, -diff);
        }
        return this;
    }

    public UiBuilder moveGroup(int x, int y, int z) {
        final Group group = getGroup();
        for (View view : group.views)
            view.translate(x, y, z);
        return this;
    }

    // Sizing

    public UiBuilder size(int w, int h) {
        getGroup().currView.setSize(w, h);
        return this;
    }

    // Other

//    public UiBuilder root() {
//        this.container = rootContainer;
//        this.currView = rootContainer;
//        return this;
//    }

    private Group getGroup() {
        return groups.peek();
    }

    private static class Group {
        ArrayList<View> views = new ArrayList<>();
        ViewContainer container;
        View baseView;
        View currView;
        View prevView;

        Group(ViewContainer container, View base, View curr, View prev) {
            this.container = container;
            this.baseView = base;
            this.currView = curr;
            this.prevView = prev;
        }

        public void add(@Nonnull View view) {
            views.add(view);
            prevView = currView;
            currView = view;
        }

        public void end() {
            for (View v : views)
                container.addView(v);
        }
    }
}
