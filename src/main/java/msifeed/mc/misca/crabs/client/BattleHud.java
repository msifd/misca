package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.ImGui;
import msifeed.mc.gui.input.KeyTracker;
import msifeed.mc.gui.nim.NimPart;
import msifeed.mc.gui.nim.NimText;
import msifeed.mc.gui.nim.NimWindow;
import msifeed.mc.misca.crabs.CrabsNetwork;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.crabs.battle.FighterContext;
import msifeed.mc.misca.crabs.character.CharacterMessage;
import msifeed.mc.misca.crabs.character.Stats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.function.Function;

public enum BattleHud {
    INSTANCE;

    private final NimWindow battleWindow = new NimWindow("Battle", this::toggleHud);
    private final int statTextWidth = 12;
    private final NimText[] statTexts = new NimText[Stats.values().length];

    private Boolean shouldDisplay = false;

    BattleHud() {
        final Function<String, Boolean> validator = s -> s.matches("\\d{0,2}");
        for (int i = 0; i < statTexts.length; i++) {
            final NimText t = new NimText(statTextWidth);
            t.validateText = validator;
            statTexts[i] = t;
        }
    }

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        if (!NimPart.focused() && KeyTracker.isTapped(CrabsKeyBinds.battleHud.getKeyCode())) {
            toggleHud();
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

            imgui.horizontalBlock();
            for (int i = 0; i < statTexts.length; i++) {
                imgui.label(Stats.values()[i].toString(), statTextWidth);
            }

            imgui.horizontalBlock();
            for (int i = 0; i < statTexts.length; i++) {
                imgui.nim(statTexts[i]);
            }

            final int inputWidth = battleWindow.getBlockContentWidth();
            imgui.verticalBlock();
            if (imgui.button("Update character", inputWidth)) {
                try {
                    byte[] stats = new byte[statTexts.length];
                    for (int i = 0; i < stats.length; i++) {
                        stats[i] = Byte.parseByte(statTexts[i].getText());
                    }
                    final CharacterMessage msg = new CharacterMessage(stats);
                    CrabsNetwork.INSTANCE.notifyServer(msg);
                } catch (NumberFormatException e) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("Fill all stats");
                }

//                FighterMessage message = new FighterMessage(inBattle ? FighterMessage.Type.LEAVE : FighterMessage.Type.JOIN);
//                CrabsNetwork.INSTANCE.notifyServer(message);
            }

//            imgui.verticalBlock();
//            if (imgui.button(inBattle ? "Stop fight" : "Start fight")) {
//                FighterMessage message = new FighterMessage(inBattle ? FighterMessage.Type.LEAVE : FighterMessage.Type.JOIN);
//                BattleNetwork.INSTANCE.notifyServer(message);
//            }
//
//            if (inBattle) {
//                if (imgui.button("Punch")) {
//                    BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Actions.test_punch));
//                }
//                if (imgui.button("Fireball")) {
//                    BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Actions.test_fireball));
//                }
//                if (imgui.button("Roll ERP")) {
//                    BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Stats.DET));
//                }
//            }

            imgui.endWindow();
        }
    }

    private void toggleHud() {
        shouldDisplay = !shouldDisplay;

        // Display empty screen with cursor
        final Minecraft mc = Minecraft.getMinecraft();
        if (shouldDisplay) mc.displayGuiScreen(EmptyGuiScreen.INSTANCE);
        else if (mc.currentScreen == EmptyGuiScreen.INSTANCE) mc.displayGuiScreen(null);
    }
}
