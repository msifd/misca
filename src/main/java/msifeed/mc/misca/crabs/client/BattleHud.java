package msifeed.mc.misca.crabs.client;

import msifeed.mc.gui.NimGui;
import msifeed.mc.gui.nim.NimText;
import msifeed.mc.gui.nim.NimWindow;
import msifeed.mc.misca.crabs.CrabsNetwork;
import msifeed.mc.misca.crabs.actions.Action;
import msifeed.mc.misca.crabs.actions.ActionManager;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.crabs.battle.FighterContext;
import msifeed.mc.misca.crabs.battle.FighterMessage;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;

public class BattleHud extends AbstractHudWindow {
    public static final BattleHud INSTANCE = new BattleHud();

    private final NimWindow battleWindow = new NimWindow(MiscaUtils.l10n("misca.crabs.battle"), () -> HudManager.INSTANCE.closeHud(INSTANCE));
    private final NimText modText = new NimText(20);

    private boolean statRollsMode = false;
    private Action.Type currActionTab = Action.Type.MELEE;

    private long lastRollTime = System.currentTimeMillis();

    private BattleHud() {
        modText.validateText = s -> s.matches("-?\\d{0,3}");
    }

    @Override
    KeyBinding getKeyBind() {
        return CrabsKeyBinds.battleHud;
    }

    @Override
    void open() {
    }

    @Override
    void close() {
    }

    @Override
    void render() {
        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.thePlayer;
        final BattleManager bm = BattleManager.INSTANCE;
        final FighterContext context = bm.getContext(player.getUniqueID());

        final boolean inBattle = context != null;
        final boolean isLeaving = inBattle && context.status == FighterContext.Status.LEAVING;

        final NimGui nimgui = NimGui.INSTANCE;
        nimgui.beginWindow(battleWindow);

        nimgui.horizontalBlock();
        final String controlButtonKey = "misca.crabs." + (inBattle ? isLeaving ? "cancel" : "leave_battle" : "join_battle");
        if (nimgui.button(MiscaUtils.l10n(controlButtonKey))) {
            FighterMessage message = new FighterMessage(inBattle ? FighterMessage.Type.LEAVE : FighterMessage.Type.JOIN);
            CrabsNetwork.INSTANCE.sendToServer(message);
        }

        if (inBattle) {
            final String modeTitle = statRollsMode
                    ? MiscaUtils.l10n("misca.crabs.battle")
                    : MiscaUtils.l10n("misca.crabs.stats");
            if (nimgui.button(modeTitle)) {
                statRollsMode = !statRollsMode;
            }
        }

        if (inBattle && !statRollsMode) {

            final FighterContext actor = context.control == null
                    ? context
                    : bm.getContext(context.control);
            final boolean isAttack = actor.status == FighterContext.Status.ACT && actor.target == null;

            renderStatus(nimgui, actor);

            if (actor.canSelectAction()) {
                renderActionTabs(nimgui, isAttack);
                renderActions(nimgui);
                renderPlayerModifier(nimgui);
            } else {
                // TODO display selected action
            }
        } else {
            renderStatRolls(nimgui);
            renderPlayerModifier(nimgui);
        }

        nimgui.endWindow();
    }

    private void renderStatRolls(NimGui nimgui) {
        final Stats[] stats = Stats.values();
        for (int i = 0; i < stats.length; i++) {
            if (i % 3 == 0) nimgui.horizontalBlock();
            if (nimgui.button(stats[i].toString())) {
                final long now = System.currentTimeMillis();
                if (now - lastRollTime > 2000) {
                    lastRollTime = now;
                    CrabsNetwork.INSTANCE.sendToServer(new FighterMessage(stats[i], getModifierInput()));
                }
            }
        }
    }

    private void renderStatus(NimGui nimgui, FighterContext actor) {
        String status = actor.entity.getCommandSenderName() + ": " + actor.status;
        if (actor.target != null) {
            final FighterContext target = BattleManager.INSTANCE.getContext(actor.target);
            status += ". Target: " + target.entity.getCommandSenderName();
        }
        nimgui.label(status, 0, 2);
    }

    private void renderActionTabs(NimGui nimgui, boolean isAttack) {
        // Пробел после переключателей режимов
        nimgui.horizontalBlock();
        battleWindow.consume(0, battleWindow.nextElemY(), 0, 1);

        // Горизонтальные блоки задаются в цикле
        final Action.Type[] types = Action.Type.values();
        for (int i = 0; i < types.length; i++) {
            if (i % 3 == 0) nimgui.horizontalBlock();
            if (types[i] == Action.Type.PASSIVE && isAttack) continue;
            if (nimgui.button(types[i].pretty())) {
                currActionTab = types[i];
            }
        }
    }

    private void renderActions(NimGui nimgui) {
        final Collection<Action> stubs = ActionManager.INSTANCE.stubs().get(currActionTab);
        // Пробел между категориями и экшнами
        nimgui.horizontalBlock();
        battleWindow.consume(0, battleWindow.nextElemY(), 0, 1);

        nimgui.horizontalBlock();
        for (Action action : stubs) {
            if (nimgui.button(action.pretty())) {
                CrabsNetwork.INSTANCE.sendToServer(new FighterMessage(action.name, getModifierInput()));
            }
        }
    }

    private void renderPlayerModifier(NimGui nimgui) {
        nimgui.horizontalBlock();
        nimgui.label(MiscaUtils.l10n("misca.crabs.player_mod"));
        nimgui.nim(modText);
    }

    private int getModifierInput() {
        try {
            final String modStr = modText.getText();
            return (modStr.isEmpty() || modStr.equals("-"))
                    ? 0
                    : Integer.parseInt(modStr);
        } catch (NumberFormatException ignore) {
            return 0;
        }
    }
}
