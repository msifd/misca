package msifeed.mellow.view.text;

import msifeed.mellow.render.RenderParts;
import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.render.RenderUtils;
import msifeed.mellow.utils.Geom;
import msifeed.mellow.view.InputHandler;
import msifeed.mellow.view.View;
import msifeed.mellow.view.text.backend.TextEditorBackend;
import msifeed.mellow.view.text.backend.TextInputHelper;

public class TextInput extends View implements InputHandler.Keyboard, InputHandler.MouseClick {
    protected TextEditorBackend backend = new TextEditorBackend();
    protected TextInputHelper inputHelper = new TextInputHelper(backend, TextInputHelper.NavMode.LINES);
    protected RenderParts.TextPref pref = new RenderParts.TextPref();

    protected long lastTimePressed = 0;

    public boolean isEmpty() {
        return backend.isEmpty();
    }

    public String getText() {
        return backend.toJoinedString();
    }

    public void insert(CharSequence text) {
        backend.insert(text);
    }

    public void clear() {
        backend.clear();
    }

    public TextEditorBackend getBackend() {
        return backend;
    }

    public RenderParts.TextPref getRenderPref() {
        return pref;
    }

    @Override
    public boolean onKeyboard(char c, int key) {
        lastTimePressed = System.currentTimeMillis();
        return inputHelper.onKeyboard(c, key);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        inputHelper.setCursorAtPos(mouseX - this.geometry.x, mouseY - this.geometry.y);
    }

    @Override
    public void render() {
        RenderShapes.rect(getRenderGeom(), 0xbb000000);
        RenderParts.lines(backend.getViewLines(), getTextGeom(), pref);
        renderCursor();
    }

    protected void renderCursor() {
        if (!isFocused() || (System.currentTimeMillis() - lastTimePressed) % 1000 > 500)
            return;

        final int lineHeight = RenderUtils.lineHeight() + pref.gap;

//        final Geom cursorGeom = this.getGeomWithMargin();
        final Geom geom = getTextGeom();
        geom.translate(backend.getCursorXOffset(), lineHeight * inputHelper.getCursorLineInView() - 2);
        geom.setSize(2, lineHeight);

        RenderShapes.rect(geom, pref.color);
    }

    protected Geom getTextGeom() {
        final Geom geom = getRenderGeom();
        geom.translate(2, 3, 0);
        return geom;
    }
}