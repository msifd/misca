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

    protected Geom textGeom = new Geom();
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
    public void onKeyboard(char c, int key) {
        inputHelper.onKeyboard(c, key);
        lastTimePressed = System.currentTimeMillis();
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        inputHelper.setCursorAtPos(mouseX - this.geometry.x, mouseY - this.geometry.y);
    }

    @Override
    public void setPos(int x, int y, int z) {
        super.setPos(x, y, z);
        textGeom.setPos(x + 2, y + 3, z);
    }

    @Override
    public void render() {
        RenderShapes.rect(this.geometry, 0xbb000000);
        RenderParts.lines(backend.getViewLines(), textGeom, pref);
        renderCursor();
    }

    protected void renderCursor() {
        if (!isFocused() || (System.currentTimeMillis() - lastTimePressed) % 1000 > 500)
            return;

        final int lineHeight = RenderUtils.lineHeight() + pref.gap;

//        final Geom cursorGeom = this.getGeomWithMargin();
        final Geom cursorGeom = textGeom.clone();
        cursorGeom.translate(backend.getCursorXOffset(), lineHeight * inputHelper.getCursorLineInView() - 2);
        cursorGeom.setSize(2, lineHeight);

        RenderShapes.rect(cursorGeom, pref.color);
    }
}
