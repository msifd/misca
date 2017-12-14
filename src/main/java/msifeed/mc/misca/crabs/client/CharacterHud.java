package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.ImGui;
import msifeed.mc.gui.input.KeyTracker;
import msifeed.mc.gui.nim.NimPart;
import msifeed.mc.gui.nim.NimText;
import msifeed.mc.gui.nim.NimWindow;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.CharacterManager;
import msifeed.mc.misca.crabs.character.Stats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.UUID;
import java.util.function.Function;

public enum CharacterHud {
    INSTANCE;

    private final int statTextWidth = 16;
    private final NimText[] statTexts = new NimText[Stats.values().length];
    private Boolean shouldDisplay = false;
    private UUID latestCharUuid;
    private Character character;
    private boolean fetching = false;
    private final NimWindow window = new NimWindow("Character", this::toggleHud);

    CharacterHud() {
        final Function<String, Boolean> validator = s -> s.matches("\\d{0,2}");
        for (int i = 0; i < statTexts.length; i++) {
            final NimText t = new NimText(statTextWidth);
            t.validateText = validator;
            t.centerByWidth = true;
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
        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.thePlayer;

        if (BattleManager.INSTANCE.isBattling(player)) {
            toggleHud();
            return;
        }

        latestCharUuid = player.getUniqueID();
        final CharacterManager cm = CharacterManager.INSTANCE;
        final Character characterResponse = cm.get(latestCharUuid);

        final ImGui imgui = ImGui.INSTANCE;
        imgui.newFrame();

        imgui.beginWindow(window);

        // Fetch char
        if (characterResponse == null && character == null) {
            if (!fetching) {
                fetching = true;
                cm.request(latestCharUuid);
            }
            imgui.label("Fetching...");
            imgui.endWindow();
            return;
        }

        // Got char, fill inputs
        final Stats[] statValues = Stats.values();
        if (characterResponse != null && character != characterResponse) {
            character = characterResponse;
            for (int i = 0; i < statValues.length; i++)
                statTexts[i].setText(Integer.toString(character.stat(statValues[i])));
        }

        imgui.horizontalBlock();
        for (int i = 0; i < statTexts.length; i++) {
            imgui.label(statValues[i].toString(), statTextWidth);
        }

        imgui.horizontalBlock();
        for (NimText statText : statTexts) {
            imgui.nim(statText);
        }

        final int inputWidth = window.getBlockContentWidth();
        imgui.verticalBlock();
        if (imgui.button("Update character", inputWidth)) {
            try {
                int[] stats = new int[statTexts.length];
                for (int i = 0; i < stats.length; i++) stats[i] = Byte.parseByte(statTexts[i].getText());
                character.fill(stats);
                CharacterManager.INSTANCE.requestUpdate(player.getUniqueID(), character);
            } catch (NumberFormatException e) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("Fill all stats");
            }
        }

        imgui.endWindow();
    }

    private void toggleHud() {
        shouldDisplay = !shouldDisplay;

        // Display empty screen with cursor
        final Minecraft mc = Minecraft.getMinecraft();
        if (shouldDisplay) mc.displayGuiScreen(EmptyGuiScreen.INSTANCE);
        else if (mc.currentScreen == EmptyGuiScreen.INSTANCE) mc.displayGuiScreen(null);

        if (!shouldDisplay) {
            latestCharUuid = null;
            character = null;
            fetching = false;
        }
    }
}
