package msifeed.mellow.utils;

import msifeed.mellow.view.View;
import msifeed.mellow.view.ViewContainer;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class UiBuilder {
    private final ViewContainer rootContainer;
    private ViewContainer container;
    private View lastView = null;
    private View currView;

    public static UiBuilder of(ViewContainer container) {
        return new UiBuilder(container);
    }

    private UiBuilder(ViewContainer container) {
        this.rootContainer = container;
        this.container = container;
        this.currView = container;
    }

    public UiBuilder add(@Nonnull View view) {
        container.addView(view);
        lastView = currView;
        currView = view;
        return this;
    }

    public UiBuilder center(Direction direction) {
        final Geom cg = container.getBaseGeom();
        final Geom vg = currView.getBaseGeom();
        if (direction.isHorizontal()) vg.x = (cg.w - vg.w) / 2;
        if (direction.isVertical()) vg.y = (cg.h - vg.h) / 2;
        return this;
    }

    public UiBuilder below() {
        final Geom lg = lastView.getBaseGeom();
        currView.setPos(lg.x, lg.y + lg.h + 1, lg.z);
        return this;
    }

    public UiBuilder at(int x, int y) {
        currView.setPos(x, y, currView.getBaseGeom().z);
        return this;
    }

    public UiBuilder at(int x, int y, int z) {
        currView.setPos(x, y, z);
        return this;
    }

    public UiBuilder size(int w, int h) {
        currView.setSize(w, h);
        return this;
    }

    public <T> UiBuilder forEach(Iterable<T> iterable, BiConsumer<UiBuilder, T> consumer) {
        for (T val : iterable)
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

    public UiBuilder root() {
        this.container = rootContainer;
        this.currView = rootContainer;
        return this;
    }

    public void build() {

    }
}
