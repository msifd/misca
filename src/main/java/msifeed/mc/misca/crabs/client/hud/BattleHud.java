package msifeed.mc.misca.crabs.client.hud;

import msifeed.mc.gui.NimGui;
import msifeed.mc.gui.nim.NimText;
import msifeed.mc.gui.nim.NimWindow;
import msifeed.mc.misca.crabs.CrabsNetwork;
import msifeed.mc.misca.crabs.action.Action;
import msifeed.mc.misca.crabs.action.ActionManager;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.client.CrabsKeyBinds;
import msifeed.mc.misca.crabs.context.Context;
import msifeed.mc.misca.crabs.context.ContextManager;
import msifeed.mc.misca.crabs.fight.FighterMessage;
import msifeed.mc.misca.crabs.rules.FistFight;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.Iterator;

public class BattleHud extends AbstractHudWindow {
    public static final BattleHud INSTANCE = new BattleHud();

    private final NimWindow battleWindow = new NimWindow(MiscaUtils.l10n("misca.crabs.battle"), () -> HudManager.INSTANCE.closeHud(INSTANCE));
    private final NimText modText = new NimText(20);

    private Context context = null;
    private RollTab rollTab = RollTab.STATS;
    private Action.Type currActionTab = Action.Type.MELEE;

    private long lastRollTime = System.currentTimeMillis();

    private BattleHud() {
        modText.validateText = s -> s.matches("(-?\\d{0,3})?");
        modText.onUnfocus = s -> {
            if (context == null) return;
            final int mod = getModifierInput();
            if (mod != context.modifier)
                CrabsNetwork.INSTANCE.sendToServer(new FighterMessage(mod));
        };
    }

    @Override
    KeyBinding getKeyBind() {
        return CrabsKeyBinds.battleHud;
    }

    @Override
    void open() {
        final boolean isFighting = context != null && context.status.isFighting();
        if (!isFighting && rollTab == RollTab.BATTLE)
            rollTab = rollTab.next();
    }

    @Override
    void close() {
    }

    @Override
    void render() {
        final boolean wasInFight = context != null && context.status.isFighting();

        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.thePlayer;
        final ContextManager cm = ContextManager.INSTANCE;
        context = cm.getContext(player.getUniqueID());

        final boolean isFighting = context != null && context.status.isFighting();

        final NimGui nimgui = NimGui.INSTANCE;
        nimgui.beginWindow(battleWindow);

        // Super кнопки
        {
            nimgui.horizontalBlock();

            final boolean isLeaving = context != null && context.status == Context.Status.LEAVING;
            final String controlButtonKey = "misca.crabs." + (isFighting ? isLeaving ? "cancel" : "leave_fight" : "join_fight");
            if (nimgui.button(MiscaUtils.l10n(controlButtonKey))) {
                FighterMessage message = new FighterMessage(isFighting ? FighterMessage.Type.LEAVE : FighterMessage.Type.JOIN);
                CrabsNetwork.INSTANCE.sendToServer(message);
                if (!isFighting) rollTab = RollTab.BATTLE;
            }

            if (nimgui.button(rollTab.title)) {
                rollTab = rollTab.next();
                if (!isFighting && rollTab == RollTab.BATTLE)
                    rollTab = rollTab.next();
            }
        }

        if (isFighting && !wasInFight)
            rollTab = RollTab.BATTLE;

        if (isFighting && rollTab == RollTab.BATTLE) {
            final Context actor = context.puppet == null ? context : cm.getContext(context.puppet);

            if (!modText.inFocus())
                modText.setText(actor.modifier == 0 ? "" : Integer.toString(actor.modifier));

            // При защите таргет уже указвает на нападающего
            final boolean isAttack = actor.status == Context.Status.ACTIVE && actor.target == null;

            renderStatus(nimgui, actor);

            if (actor.canSelectAction()) {
                renderActionTabs(nimgui, isAttack);
                renderActions(nimgui, isAttack);
                renderPlayerModifier(nimgui);
            }

            if (actor.status != Context.Status.LEAVING)
                renderManual(nimgui, actor);

            // Позволяем отменить свою атаку
            if (actor.status == Context.Status.WAIT) {
                if (nimgui.button(MiscaUtils.l10n("misca.crabs.abort"), 100)) {
                    CrabsNetwork.INSTANCE.sendToServer(new FighterMessage(FighterMessage.Type.RESET));
                }
            }
        } else if (rollTab == RollTab.STATS) {
            renderStatRolls(nimgui);
            renderPlayerModifier(nimgui);
        } else if (rollTab == RollTab.FISTS) {
            renderFistFightRolls(nimgui);
            renderPlayerModifier(nimgui);
        }

        nimgui.endWindow();
    }

    private void renderStatRolls(NimGui nimgui) {
        nimgui.horizontalBlock();
        // Пробел между категориями
        battleWindow.consume(0, battleWindow.nextElemY(), 0, 1);

        final Stats[] stats = Stats.values();
        for (int i = 0; i < stats.length; i++) {
            if (i % 2 == 0) nimgui.horizontalBlock();
            if (nimgui.button(stats[i].toString())) {
                final long now = System.currentTimeMillis();
                if (now - lastRollTime > 2000) {
                    lastRollTime = now;
                    CrabsNetwork.INSTANCE.sendToServer(new FighterMessage(stats[i], getModifierInput()));
                }
            }
        }
    }

    private void renderFistFightRolls(NimGui nimgui) {
        nimgui.horizontalBlock();
        // Пробел между категориями
        battleWindow.consume(0, battleWindow.nextElemY(), 0, 1);

        final FistFight.Action[] actions = FistFight.Action.values();
        for (int i = 0; i < actions.length; i++) {
            if (i % 2 == 0) nimgui.horizontalBlock();
            if (nimgui.button(actions[i].pretty())) {
                final long now = System.currentTimeMillis();
                if (now - lastRollTime > 2000) {
                    lastRollTime = now;
                    CrabsNetwork.INSTANCE.sendToServer(new FighterMessage(actions[i], getModifierInput()));
                }
            }
        }
    }

    private void renderStatus(NimGui nimgui, Context actor) {
        if (actor.entity == null) return;

        nimgui.verticalBlock();
        String status = actor.entity.getCommandSenderName()
                + ": " + MiscaUtils.l10n("misca.crabs.gui.status." + actor.status.toString());

        if (actor.knockedOut) {
            status += MiscaUtils.l10n("misca.crabs.gui.knocked_out");
        } else if (actor.target != null) {
            final Context target = ContextManager.INSTANCE.getContext(actor.target);
            if (target.entity != null)
                status += MiscaUtils.l10n("misca.crabs.gui.target", target.entity.getCommandSenderName());
        }
        nimgui.label(status, 0, 2);

        if (!actor.buffNames.isEmpty()) {
            final StringBuilder buffs = new StringBuilder(MiscaUtils.l10n("misca.crabs.gui.buffs"));
            buffs.append(' ');

            buffs.append(actor.buffNames.getFirst());
            if (actor.buffNames.size() > 1) {
                actor.buffNames.listIterator(1).forEachRemaining(s -> {
                    buffs.append(", ");
                    buffs.append(s);
                });
            }

            nimgui.label(buffs.toString());
        }
    }

    private void renderActionTabs(NimGui nimgui, boolean isAttack) {
        // Пробел после переключателей режимов
        nimgui.horizontalBlock();
        battleWindow.consume(0, battleWindow.nextElemY(), 0, 1);

        if (currActionTab.defencive() && isAttack)
            currActionTab = Action.Type.MELEE;

        // Горизонтальные блоки задаются в цикле
        final Action.Type[] types = Action.Type.values();
        for (int i = 0; i < types.length; i++) {
            if (i % 2 == 0) nimgui.horizontalBlock();
            if (types[i].defencive() && isAttack) continue;
            if (nimgui.button(types[i].pretty())) {
                currActionTab = types[i];
            }
        }
    }

    private void renderActions(NimGui nimgui, boolean isAttack) {
//        nimgui.horizontalBlock();

        nimgui.horizontalBlock();
        // Пробел между категориями
        battleWindow.consume(0, battleWindow.nextElemY(), 0, 1);

        final Collection<Action> stubs = ActionManager.INSTANCE.stubs().get(currActionTab);
        Iterator<Action> it = stubs.iterator();

        int pos = 0;
        while (it.hasNext()) {
            if (pos % 2 == 0) nimgui.horizontalBlock();
            final Action action = it.next();
            if (isAttack && action.isDefencive()) continue;
            if (nimgui.button(action.pretty())) {
                CrabsNetwork.INSTANCE.sendToServer(new FighterMessage(action.name, getModifierInput()));
            }
            pos++;
        }
    }

    private void renderPlayerModifier(NimGui nimgui) {
        nimgui.horizontalBlock();
        nimgui.label(MiscaUtils.l10n("misca.crabs.player_mod"));
        nimgui.nim(modText);
    }

    private void renderManual(NimGui nimgui, Context context) {
        if (context.knockedOut) return;

        nimgui.verticalBlock();
        if (context.action == null) {
            nimgui.label(MiscaUtils.l10n("misca.crabs.gui.no_action"));
            return;
        }

        if (context.modifier == 0)
            nimgui.label(MiscaUtils.l10n("misca.crabs.gui.action", context.action.pretty()));
        else
            nimgui.label(MiscaUtils.l10n("misca.crabs.gui.action_mod", context.action.pretty(), context.modifier));
        nimgui.label(MiscaUtils.l10n("misca.crabs.gui.describe"));

        if (context.described && !context.action.isDefencive()) {
            if (context.damageDealt == 0)
                nimgui.label(MiscaUtils.l10n("misca.crabs.gui.no_damage"));
            else
                nimgui.label(MiscaUtils.l10n("misca.crabs.gui.damage", context.damageDealt));
        }

        if (context.status == Context.Status.WAIT)
            nimgui.label(MiscaUtils.l10n("misca.crabs.gui.wait"));
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

    private enum RollTab {
        STATS, BATTLE, FISTS;

        String title = I18n.format("misca.crabs." + toString().toLowerCase());

        public RollTab next() {
            final RollTab[] v = RollTab.values();
            final int n = ordinal() + 1;
            return n < v.length ? v[n] : v[0];
        }
    }
}
