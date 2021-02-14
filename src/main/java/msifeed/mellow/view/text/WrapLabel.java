package msifeed.mellow.view.text;

import msifeed.mellow.render.RenderParts;
import msifeed.mellow.utils.Geom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;

public class WrapLabel extends Label {
    protected List<String> lines;

    public WrapLabel(String text, int color) {
        super(text, color);
    }

    public List<String> getLines() {
        return lines;
    }

    protected void updateLines() {
        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        this.lines = fr.listFormattedStringToWidth(text, this.geometry.w);
        this.geometry.h = this.lines.size() * fr.FONT_HEIGHT;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        updateLines();
    }

    @Override
    public void setSize(int w, int h) {
        if (this.geometry.w != w) {
            this.geometry.w = w;
            updateLines();
        }
    }

    @Override
    public void render(Geom geom) {
        RenderParts.lines(lines, geom, this.pref);
    }
}
