package msifeed.mc.gui.widgets;

import msifeed.mc.gui.GraphicsHelper;
import msifeed.mc.gui.events.KeyEvent;
import msifeed.mc.gui.events.MouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class TextInputWidget extends BaseWidget {
    protected String text = "";
    protected Color text_color = Color.WHITE;
    protected int padding = 2;
    private int scrollOffset = 0;
    private int cursor = 0;
    private int frame_counter = 0;

    public TextInputWidget(int width, int height) {
        super(width, height);
    }

    @Override
    public void render(Minecraft mc, int mouseX, int mouseY, float tick) {
        frame_counter++;
        render_background(mc, mouseX, mouseY, tick);

        final FontRenderer fr = mc.fontRenderer;
        int textX = posX + padding;
        int textY = posY + (height - fr.FONT_HEIGHT) / 2 + padding;

        String str = fr.trimStringToWidth(text.substring(scrollOffset), width);
        fr.drawString(str, textX, textY, text_color.getRGB());

        if (isFocused() && frame_counter / 10 % 2 == 0) {
            if (cursor >= text.length())
                fr.drawString("_", textX + fr.getStringWidth(text), textY, text_color.brighter().getRGB());
            else
                GraphicsHelper.drawColoredBox(text_color.brighter(), textX + fr.getStringWidth(text.substring(0, cursor)), textY - 1, zLevel, 1, fr.FONT_HEIGHT + 2);
        }
    }

    protected void render_background(Minecraft mc, int mouseX, int mouseY, float tick) {
        GraphicsHelper.drawColoredBox(Color.LIGHT_GRAY, posX, posY, zLevel - 1, width, height);
        GraphicsHelper.drawColoredBox(Color.BLACK, posX + 1, posY + 1, zLevel, width - 2, height - 2);
    }

    @Override
    public void onMouseEvent(MouseEvent event) {
        if (event.type != MouseEvent.Type.PRESS) return;
        setFocused();

        int clickX = event.mouseX - posX + padding;
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        String str = fr.trimStringToWidth(text.substring(scrollOffset), width);
        setCursor(fr.trimStringToWidth(str, clickX).length() + scrollOffset);
    }

    @Override
    public void onKeyEvent(KeyEvent event) {
        if (!isFocused()) return;

        switch (event.key) {
            case Keyboard.KEY_LEFT:
                setCursor(cursor - 1);
                break;
            case Keyboard.KEY_RIGHT:
                setCursor(cursor + 1);
                break;
            case Keyboard.KEY_HOME:
                setCursor(0);
                break;
            case Keyboard.KEY_END:
                setCursor(text.length());
                break;
            case Keyboard.KEY_BACK:
                delete(-1);
                break;
            case Keyboard.KEY_DELETE:
                delete(1);
                break;
            default:
                if (ChatAllowedCharacters.isAllowedCharacter(event.character))
                    addCharAtCursor(event.character);
                break;
        }
    }

    public boolean onTextUpdate(String newText) {
        return true;
    }

    public void setCursor(int pos) {
        cursor = pos;
        if (cursor < 0) cursor = 0;
        if (cursor > text.length()) cursor = text.length();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (onTextUpdate(text)) {
            this.text = text;
            this.cursor = text.length();
        }
    }

    public void addCharAtCursor(char c) {
        String newText = new StringBuilder(this.text).insert(cursor, c).toString();
        if (onTextUpdate(newText)) {
            this.text = newText;
            this.cursor++;
        }
    }

    public void delete(int relative) {
        int rel = cursor + relative;
        if (rel < 0 || rel > this.text.length()) return;
        if (relative < 0) this.text = this.text.substring(0, rel) + this.text.substring(cursor);
        else this.text = this.text.substring(0, cursor) + this.text.substring(rel);
        if (relative < 0) this.cursor = rel;
    }
}
