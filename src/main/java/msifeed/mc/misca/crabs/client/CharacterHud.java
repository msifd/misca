package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.NimGui;
import msifeed.mc.gui.input.KeyTracker;
import msifeed.mc.gui.nim.NimPart;
import msifeed.mc.gui.nim.NimText;
import msifeed.mc.gui.nim.NimWindow;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.CharacterManager;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.UUID;
import java.util.function.Function;

public enum CharacterHud {
    INSTANCE;

    private boolean isOpened = false;
    private final int statTextWidth = 16;
    private final NimText[] statTexts = new NimText[Stats.values().length];
    private final NimWindow window = new NimWindow("Character", this::close);

    private EntityLivingBase entity;
    private UUID entityUuid;
    private Character character;

    CharacterHud() {
        final Function<String, Boolean> validator = s -> s.matches("\\d{0,2}");
        for (int i = 0; i < statTexts.length; i++) {
            final NimText t = new NimText(statTextWidth);
            t.validateText = validator;
            t.centerByWidth = true;
            statTexts[i] = t;
        }
    }

    public void open(EntityLivingBase entity) {
        isOpened = true;
        window.title = "Character: " + entity.getCommandSenderName();

        this.entity = entity;
        this.entityUuid = EntityUtils.getUuid(entity);
        this.character = null; // Сбрасываем перед запросом

        // Просим чара, после чего заполняем поля для ввода
        CharacterManager.INSTANCE.fetch(entityUuid, c -> {
            character = c;
            character.name = entity.getCommandSenderName(); // Называем чара только при редактировании
            character.isPlayer = entity instanceof EntityPlayer; // Чары по-умолчанию не игроки, исправляем
            final Stats[] statValues = Stats.values();
            for (int i = 0; i < statValues.length; i++)
                statTexts[i].setText(Integer.toString(character.stat(statValues[i])));
        });

        // Если никакого экрана не открыто, то открываем свой пустой
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null) mc.displayGuiScreen(EmptyGuiScreen.INSTANCE);
    }

    public void close() {
        isOpened = false;

        // Manually release inputs' focus to be able use hotkey again
        for (NimText t : statTexts)
            if (t.inFocus()) t.releaseFocus();

        // Close special empty screen if it active
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == EmptyGuiScreen.INSTANCE) mc.displayGuiScreen(null);
    }

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        // Во время боя изменять самому себе статы нельзя.
        // Распологается здесь чтобы окошко закрывалось при входе в бой.
        final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (entity == player && BattleManager.INSTANCE.isBattling(player)) {
            close();
            return;
        }

        if (!NimPart.focused() && KeyTracker.isTapped(CrabsKeyBinds.charHud.getKeyCode())) {
            if (isOpened) close();
            else open(player);
        }

        if (isOpened) render();
    }

    protected void render() {
        final NimGui nimgui = NimGui.INSTANCE;
        nimgui.beginWindow(window);

        // Ждем получения чара
        if (character == null) {
            nimgui.label("Fetching...");
            nimgui.endWindow();
            return;
        }

        nimgui.horizontalBlock();
        final Stats[] statValues = Stats.values();
        for (int i = 0; i < statTexts.length; i++) {
            nimgui.label(statValues[i].toString(), statTextWidth);
        }

        nimgui.horizontalBlock();
        for (NimText statText : statTexts) {
            nimgui.nim(statText);
        }

        final int inputWidth = window.getBlockContentWidth();
        nimgui.verticalBlock();
        if (nimgui.button("Update character", inputWidth)) {
            try {
                int[] stats = new int[statTexts.length];
                for (int i = 0; i < stats.length; i++) stats[i] = Byte.parseByte(statTexts[i].getText());
                character.fill(stats);
                CharacterManager.INSTANCE.requestUpdate(entityUuid, character);
            } catch (NumberFormatException e) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("Fill all stats");
            }
        }

        nimgui.endWindow();
    }
}
