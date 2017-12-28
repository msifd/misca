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
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;

public class BattleHud extends AbstractHudWindow {
    public static final BattleHud INSTANCE = new BattleHud();

    private final NimWindow battleWindow = new NimWindow(MiscaUtils.l10n("misca.crabs.battle"), () -> HudManager.INSTANCE.closeHud(INSTANCE));
    private final NimText modText = new NimText(20);

    private boolean diceRollMode = false;
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
        final ContextManager cm = ContextManager.INSTANCE;
        final Context context = cm.getContext(player.getUniqueID());

        final boolean isFighting = context != null && context.status.isFighting();

        final NimGui nimgui = NimGui.INSTANCE;
        nimgui.beginWindow(battleWindow);

        // Super кнопка
        {
            nimgui.horizontalBlock();

            final boolean isLeaving = context != null && context.status == Context.Status.LEAVING;
            final String controlButtonKey = "misca.crabs." + (isFighting ? isLeaving ? "cancel" : "leave_fight" : "join_fight");
            if (nimgui.button(MiscaUtils.l10n(controlButtonKey))) {
                FighterMessage message = new FighterMessage(isFighting ? FighterMessage.Type.LEAVE : FighterMessage.Type.JOIN);
                CrabsNetwork.INSTANCE.sendToServer(message);
            }
        }

        if (isFighting) {
            final String modeTitle = diceRollMode
                    ? MiscaUtils.l10n("misca.crabs.battle")
                    : MiscaUtils.l10n("misca.crabs.dices");
            if (nimgui.button(modeTitle)) {
                diceRollMode = !diceRollMode;
            }
        }

        if (isFighting && !diceRollMode) {
            final Context actor = context.puppet == null ? context : cm.getContext(context.puppet);
            // При защите таргет уже указвает на нападающего
            final boolean isAttack = actor.status == Context.Status.ACTIVE && actor.target == null;

            renderStatus(nimgui, actor);

            if (actor.canSelectAction()) {
                renderActionTabs(nimgui, isAttack);
                renderActions(nimgui);
                renderPlayerModifier(nimgui);
            } else {
                // TODO display selected action
            }

            renderManual(nimgui, actor);
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

        if (currActionTab.dealNoDamage() && isAttack)
            currActionTab = Action.Type.MELEE;

        // Горизонтальные блоки задаются в цикле
        final Action.Type[] types = Action.Type.values();
        for (int i = 0; i < types.length; i++) {
            if (i % 3 == 0) nimgui.horizontalBlock();
            if (types[i].dealNoDamage() && isAttack) continue;
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

        if (context.described && !context.action.dealNoDamage()) {
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
}
