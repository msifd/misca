package msifeed.mc.gui;

import com.google.common.collect.Lists;
import msifeed.mc.gui.events.KeyEvent;
import msifeed.mc.gui.events.MouseEvent;
import msifeed.mc.gui.widgets.IWidget;
import msifeed.mc.gui.widgets.IWidgetComposite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class WidgetGuiScreen extends GuiScreen implements IWidgetComposite {
    protected List<IWidget> widgets = Lists.newArrayList();

    @Override
    public void initGui() {
        for (IWidget w : widgets) w.update();
    }

    @Override
    protected void keyTyped(char character, int key) {
        super.keyTyped(character, key);
        onKeyEvent(new KeyEvent(character, key));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        onMouseEvent(new MouseEvent(mouseX, mouseY, button, MouseEvent.Type.PRESS));
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long time) {
        onMouseEvent(new MouseEvent(mouseX, mouseY, button, MouseEvent.Type.MOVE));
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        onMouseEvent(new MouseEvent(mouseX, mouseY, button, MouseEvent.Type.RELEASE));
    }

    @Override
    public void render(Minecraft mc, int mouseX, int mouseY, float tick) {
        for (IWidget w : widgets) w.render(mc, mouseX, mouseY, tick);
    }

    @Override
    public int getPosX() {
        return 0;
    }

    @Override
    public void setPosX(int posX) {
    }

    @Override
    public int getPosY() {
        return 0;
    }

    @Override
    public void setPosY(int posY) {
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public IWidgetComposite getParent() {
        return null;
    }

    @Override
    public void setParent(IWidgetComposite parent) {
        throw new RuntimeException(WidgetGuiScreen.class.getSimpleName() + " is a top-level widget container and can't have any parents!");
    }

    @Override
    public List<IWidget> getChildren() {
        return widgets;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float tick) {
        render(mc, mouseX, mouseY, tick);
    }

}
