package ru.ariadna.misca.gui.elements;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.ElementHandler;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class WindowExtElement<T extends GuiScreen> extends Element<T> {
    private String name;
    private float dragOffsetX;
    private float dragOffsetY;
    private boolean isDragging;
    private Function<WindowExtElement, Boolean> closeWindow;

    private boolean isDraggable;

//    private List<Element<T>> elementList = new ArrayList<>();

    public WindowExtElement(T gui, String name, int width, int height) {
        this(gui, name, width, height, gui.width / 2 - width / 2, gui.height / 2 - height / 2, null);
    }

    public WindowExtElement(T gui, String name, int width, int height, Function<WindowExtElement, Boolean> closeWindow) {
        this(gui, name, width, height, gui.width / 2 - width / 2, gui.height / 2 - height / 2, closeWindow);
    }

    public WindowExtElement(T gui, String name, int width, int height, int posX, int posY, Function<WindowExtElement, Boolean> closeWindow) {
        super(gui, posX, posY, width, height);
        this.name = name;
        this.closeWindow = closeWindow;
        if (closeWindow != null) {
            this.addElement(new ButtonElement<>(gui, "x", this.getWidth() - 14, 0, 14, 14, (v) -> {
                onWindowClose();
                closeWindow.apply(this);
                return true;
            }).withColorScheme(ButtonElement.CLOSE));
        }
    }

    public void addElement(Element<T> element) {
        element.withParent(this);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        GL11.glPushMatrix();
        this.startScissor();
        this.drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), LLibrary.CONFIG.getPrimaryColor());
        this.drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), 14, LLibrary.CONFIG.getAccentColor());
        this.drawString(this.name, this.getPosX() + 2.0F, this.getPosY() + 3.0F, LLibrary.CONFIG.getTextColor(), false);
//        for (Element<T> element : this.elementList) {
//            element.render(mouseX, mouseY, partialTicks);
//        }
        GL11.glPopMatrix();
        this.endScissor();
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (button != 0 || !this.isSelected(mouseX, mouseY)) {
            return false;
        }
        if (mouseY < this.getPosY() + 14) {
            this.dragOffsetX = mouseX - this.getPosX();
            this.dragOffsetY = mouseY - this.getPosY();
            this.isDragging = true;
            ElementHandler.INSTANCE.removeElement(this.getGUI(), this);
            ElementHandler.INSTANCE.addElement(this.getGUI(), this);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.isDragging) {
            this.setPosX(Math.min(Math.max(mouseX - this.dragOffsetX, 0), this.getGUI().width - this.getWidth()));
            this.setPosY(Math.min(Math.max(mouseY - this.dragOffsetY, 0), this.getGUI().height - this.getHeight()));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.isDragging = false;
        return false;
    }

    @Override
    public boolean keyPressed(char character, int keyCode) {
        return false;
    }

    protected boolean onWindowClose() {
        ElementHandler.INSTANCE.removeElement(this.getGUI(), this);
        return true;
    }

    public boolean isDraggable() {
        return isDraggable;
    }

    public void setDraggable(boolean draggable) {
        isDraggable = draggable;
    }
}

