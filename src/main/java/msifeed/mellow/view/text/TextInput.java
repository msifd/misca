package msifeed.mellow.view.text;

import msifeed.mellow.render.RenderParts;
import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.render.RenderUtils;
import msifeed.mellow.utils.Geom;
import msifeed.mellow.view.IKeysHandler;
import msifeed.mellow.view.View;
import msifeed.mellow.view.text.backend.TextEditorBackend;
import msifeed.mellow.view.text.backend.TextInputHelper;

public class TextInput extends View implements IKeysHandler {
    protected TextEditorBackend backend = new TextEditorBackend();
    protected TextInputHelper inputHelper = new TextInputHelper(backend, TextInputHelper.NavMode.LINES);
    protected RenderParts.TextPref pref = new RenderParts.TextPref();

    protected long lastTimePressed = 0;

    public String getText() {
        return backend.toJoinedString();
    }

    public void insert(CharSequence text) {
        backend.insert(text);
    }

    public void clear() {
        backend.clear();
    }

    @Override
    public void onKeyboard(char c, int key) {
        inputHelper.onKeyboard(c, key);
        lastTimePressed = System.currentTimeMillis();
    }

    @Override
    public void render() {
        RenderShapes.frame(geometry, 0xffff0000, 1);
        RenderParts.lines(backend.getViewLines(), this.geometry, pref);
        renderCursor();
    }

    protected void renderCursor() {
        if ((System.currentTimeMillis() - lastTimePressed) % 1000 > 500)
            return;

        final int lineHeight = RenderUtils.lineHeight() + pref.gap;

//        final Geom cursorGeom = this.getGeomWithMargin();
        final Geom cursorGeom = this.geometry.clone();
        cursorGeom.translate(backend.getCursorXOffset(), lineHeight * inputHelper.getCursorLineInView());
        cursorGeom.setSize(2, lineHeight);

        RenderShapes.rect(cursorGeom, 0xffffffff);
    }
}
