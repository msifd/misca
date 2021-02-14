package msifeed.mellow.view.button;

import msifeed.mellow.utils.Geom;
import msifeed.mellow.view.InputHandler;
import msifeed.mellow.view.View;
import msifeed.mellow.view.text.Label;

public class ButtonLabel extends View implements InputHandler.MouseClick {
    protected Label label;
    protected Runnable callback = () -> {};

    public ButtonLabel(Label label) {
        setLabel(label);
    }

    public void setLabel(Label label) {
        this.label = label;
        setSize(label.getBaseGeom().w, label.getBaseGeom().h);
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void render(Geom geom) {
        label.render(geom);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        callback.run();
    }
}
