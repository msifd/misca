package msifeed.mc.misca.crabs.client.hud;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import msifeed.mc.gui.input.KeyTracker;
import msifeed.mc.gui.nim.NimPart;
import msifeed.mc.misca.crabs.client.EmptyGuiScreen;
import msifeed.mc.misca.crabs.client.hud.AbstractHudWindow;
import msifeed.mc.misca.crabs.client.hud.BattleHud;
import msifeed.mc.misca.crabs.client.hud.CharacterHud;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public enum HudManager {
    INSTANCE;

    private ImmutableList<AbstractHudWindow> hudWindows = ImmutableList.of(
            CharacterHud.INSTANCE,
            BattleHud.INSTANCE
    );

    public void init() {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void openHud(AbstractHudWindow hud) {
        // Если никакого экрана не открыто, то открываем свой пустой
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null) mc.displayGuiScreen(EmptyGuiScreen.INSTANCE);

        hud.open();
        hud.isOpened = true;
    }

    public void closeHud(AbstractHudWindow hud) {
        hud.close();
        hud.isOpened = false;

        // Close special empty screen if there no active huds
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == EmptyGuiScreen.INSTANCE && hudWindows.stream().noneMatch(h -> h.isOpened)) {
            mc.displayGuiScreen(null);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        final int key = Keyboard.getEventKey();
        if (!KeyTracker.isTapped(key)) return;

        // TODO Убрать
        // За одно обрабатываем всякие инпуты
        if (NimPart.focused()) {
            if (key == Keyboard.KEY_ESCAPE) NimPart.releaseFocus();
            else return;
        }

        for (AbstractHudWindow hud : hudWindows) {
            if (hud.getKeyBind().getKeyCode() == key) {
                if (hud.isOpened) closeHud(hud);
                else openHud(hud);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        for (AbstractHudWindow hud : hudWindows) {
            if (hud.isOpened) hud.render();
        }
    }
}
