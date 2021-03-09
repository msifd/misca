package msifeed.mellow;

import msifeed.mellow.view.View;

import java.lang.ref.WeakReference;
import java.util.Optional;

public enum FocusState {
    INSTANCE;

    private WeakReference<View> focus;
    private WeakReference<View> press;
    private WeakReference<View> hover;

    public void reset() {
        focus = null;
        press = null;
        hover = null;
    }

    public boolean isFocused(View view) {
        return focus != null && focus.get() == view;
    }

    public Optional<View> getFocus() {
        return focus == null ? Optional.empty() : Optional.ofNullable(focus.get());
    }

    public void setFocus(View view) {
        if (view.isFocusable())
            focus = new WeakReference<>(view);
        else
            focus = null;
    }

    public void clearFocus() {
        focus = null;
    }

    public Optional<View> getPress() {
        return press == null ? Optional.empty() : Optional.ofNullable(press.get());
    }

    public void setPress(View view) {
        press = new WeakReference<>(view);
    }

    public void clearPress() {
        press = null;
    }

    public boolean isHovered(View view) {
        return hover != null && hover.get() == view;
    }

    public void setHover(View view) {
        hover = new WeakReference<>(view);
    }

    public void clearHover() {
        hover = null;
    }
}
