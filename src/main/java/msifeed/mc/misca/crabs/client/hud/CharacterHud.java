package msifeed.mc.misca.crabs.client.hud;

import msifeed.mc.gui.NimGui;
import msifeed.mc.gui.nim.NimPart;
import msifeed.mc.gui.nim.NimText;
import msifeed.mc.gui.nim.NimWindow;
import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.CharacterManager;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.client.CrabsKeyBinds;
import msifeed.mc.misca.crabs.context.Context;
import msifeed.mc.misca.crabs.context.ContextManager;
import msifeed.mc.misca.utils.EntityUtils;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.util.UUID;
import java.util.function.Function;

public class CharacterHud extends AbstractHudWindow {
    public static final CharacterHud INSTANCE = new CharacterHud();

    private final int statTextWidth = 16;
    private final NimText[] statTexts = new NimText[Stats.values().length];
    private final NimWindow window = new NimWindow("", () -> HudManager.INSTANCE.closeHud(INSTANCE));

    private EntityLivingBase entity;
    private UUID entityUuid;
    private Character character;
    private boolean editable = true;

    private CharacterHud() {
        final Function<String, Boolean> validator = s -> s.matches("\\d{0,2}");
        for (int i = 0; i < statTexts.length; i++) {
            final NimText t = new NimText(statTextWidth);
            t.validateText = validator;
            t.centerByWidth = true;
            statTexts[i] = t;
        }
    }

    public void setEditable(boolean canEdit) {
        this.editable = canEdit;
    }

    public void setEntity(EntityLivingBase entity) {
        this.entity = entity;
        this.entityUuid = EntityUtils.getUuid(entity);
        this.character = null; // Сбрасываем перед запросом
        this.window.title = MiscaUtils.l10n("misca.crabs.character") + ' ' + entity.getCommandSenderName();

        // Просим чара, после чего заполняем поля для ввода
        CharacterManager.INSTANCE.fetch(entityUuid, c -> {
            character = c;
            character.name = entity.getCommandSenderName(); // Называем чара только при редактировании
            character.isPlayer = entity instanceof EntityPlayer; // Чары по-умолчанию не игроки, исправляем
            final Stats[] statValues = Stats.values();
            for (int i = 0; i < statValues.length; i++)
                statTexts[i].setText(Integer.toString(character.stat(statValues[i])));
        });
    }

    @Override
    KeyBinding getKeyBind() {
        return CrabsKeyBinds.charHud;
    }

    @Override
    void open() {
        if (entity == null)
            setEntity(Minecraft.getMinecraft().thePlayer);
    }

    @Override
    void close() {
        entity = null;
        editable = true; // По-дефолту редачить можно

        // Manually release inputs' focus to be able use hotkey again
        for (NimText t : statTexts)
            if (t.inFocus()) NimPart.releaseFocus();
    }

    @Override
    void render() {
        final Context context = ContextManager.INSTANCE.getContext(entityUuid);

        // Во время боя изменять самому себе статы нельзя.
        // Распологается здесь чтобы окошко закрывалось при входе в бой.
        final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (entity == player && context != null && context.status.isFighting()) {
            HudManager.INSTANCE.closeHud(INSTANCE);
            player.addChatMessage(new ChatComponentText(MiscaUtils.l10n("misca.crabs.cant_open_in_battle")));
            return;
        }

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

        if (editable) {
            final int inputWidth = window.getBlockContentWidth();
            nimgui.verticalBlock();
            if (nimgui.button(MiscaUtils.l10n("misca.crabs.update_char"), inputWidth)) {
                try {
                    int[] stats = new int[statTexts.length];
                    for (int i = 0; i < stats.length; i++) stats[i] = Byte.parseByte(statTexts[i].getText());
                    character.fill(stats);
                    CharacterManager.INSTANCE.requestUpdate(entityUuid, character);
                } catch (NumberFormatException e) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage(MiscaUtils.l10n("misca.crabs.fill_all_stats"));
                }
            }
        }

        nimgui.endWindow();
    }
}
