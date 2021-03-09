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
    protected Geom textOffset = new Geom(2, 2, 0, 0);

    protected int colorNormal = 0xffffffff;
    protected int colorHover = 0xff707070;
    protected int colorCursor = 0xffffffff;

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
    public boolean isFocusable() {
        return true;
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
    public void render(Geom geom) {
        RenderShapes.rect(geom, 0xbb000000);

        final Geom textGeom = geom.add(textOffset);
        final int color = isHovered() && !isFocused() ? colorHover : colorNormal;
        RenderParts.lines(backend.getViewLines(), textGeom, color, pref);
        renderCursor(textGeom);
    }

    protected void renderCursor(Geom geom) {
        if (!isFocused() || (System.currentTimeMillis() - lastTimePressed) % 1000 > 500)
            return;

        final int lineHeight = RenderUtils.lineHeight() + pref.gap;
        geom.translate(backend.getCursorXOffset(), lineHeight * inputHelper.getCursorLineInView() - textOffset.y);
        geom.setSize(1, lineHeight);
        RenderShapes.rect(geom, colorCursor);
    }
}
