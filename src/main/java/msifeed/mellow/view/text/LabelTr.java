package msifeed.mellow.view.text;

import net.minecraft.client.resources.I18n;

public class LabelTr extends Label {
    public LabelTr(String key, Object... args) {
        super(I18n.format(key, args));
    }

    public void setText(String key, Object... args) {
        super.setText(I18n.format(key, args));
    }
}
