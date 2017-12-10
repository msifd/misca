package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.ImGui;
import msifeed.mc.gui.input.KeyTracker;
import msifeed.mc.gui.nim.NimText;
import msifeed.mc.gui.nim.NimWindow;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.crabs.battle.FighterContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public enum BattleHud {
    INSTANCE;

    private Boolean shouldDisplay = false;

    private NimWindow battleWindow = new NimWindow("Battle", () -> shouldDisplay = !shouldDisplay);
    private NimText textField = new NimText();

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        if (KeyTracker.isTapped(CrabsKeyBinds.battleHud.getKeyCode())) {
            shouldDisplay = !shouldDisplay;

//            // Display cursor
//            final Minecraft mc = Minecraft.getMinecraft();
//            if (shouldDisplay) mc.displayGuiScreen(EmptyGuiScreen.INSTANCE);
//            else mc.displayGuiScreen(null);
        }

        if (shouldDisplay)
            render();
    }

    protected void render() {
        /*
        ЕРП-характеристики !!!!!!

        кнопки выбора действия
        другой худ: параметры харок

        типы: MELEE, RANGED, DEFENCE, SUPPORT, ADDITIONAL, MAGIC, USE, ?MOVE?, NONE;
         */

        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.thePlayer;
        final BattleManager bm = BattleManager.INSTANCE;
        final FighterContext context = bm.getContext(player.getUniqueID());

        final boolean inBattle = context != null;

        final ImGui imgui = ImGui.INSTANCE;
        imgui.newFrame();

        {
            imgui.beginWindow(battleWindow);

            imgui.label("Test text field:");
            imgui.nim(textField);
            imgui.label("> " + textField.getText());

            imgui.endWindow();
        }

//        final ImGui imgui = ImGui.INSTANCE;
//        imgui.newFrame();
//
//        {
//            imgui.beginWindow(battleWindow);
//
//            if (imgui.button(inBattle ? "Stop fight" : "Start fight")) {
//                FighterMessage message = new FighterMessage(inBattle ? FighterMessage.Type.LEAVE : FighterMessage.Type.JOIN);
//                BattleNetwork.INSTANCE.notifyServer(message);
//            }
//
//            imgui.label("foobar baby!!!!");
//
//            if (inBattle) {
//                if (imgui.button("Punch")) {
//                    BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Actions.test_punch));
//                }
//                if (imgui.button("Fireball")) {
//                    BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Actions.test_fireball));
//                }
//
////                imgui.nimText.textField(textField, 100, 100, 100);
//
//                if (imgui.button("Roll ERP")) {
//                    BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Stats.DET));
//                }
//            }
//
//            imgui.endWindow();
//        }
    }
}
