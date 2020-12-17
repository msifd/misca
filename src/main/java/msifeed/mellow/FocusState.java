package msifeed.mellow;

import msifeed.mellow.view.View;

import java.lang.ref.WeakReference;
import java.util.Optional;

public enum FocusState {
    INSTANCE;

    private WeakReference<View> focus;

    public boolean isFocused(View view) {
        return focus != null && focus.get() == view;
    }

    public Optional<View> getFocus() {
        return focus == null ? Optional.empty() : Optional.ofNullable(focus.get());
    }

    public void setFocus(View view) {
        focus = new WeakReference<>(view);
    }

    public void clearFocus() {
        focus = null;
    }
}
