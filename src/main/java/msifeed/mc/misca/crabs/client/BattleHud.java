package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.NimGui;
import msifeed.mc.gui.input.KeyTracker;
import msifeed.mc.gui.nim.NimPart;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.Collection;
import java.util.function.Function;

public enum BattleHud {
    INSTANCE;

    private final int statTextWidth = 16;
    private final NimText[] statTexts = new NimText[Stats.values().length];
    private Boolean shouldDisplay = false;
    private final NimWindow battleWindow = new NimWindow("Battle", this::toggleHud);

    private Action.Type currActionTab = Action.Type.MELEE;

    BattleHud() {
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
            final FighterContext actor = context.control == null
                    ? context
                    : bm.getContext(context.control);

            renderStatus(nimgui, actor);

            if (actor.canSelectAction()) {
                renderActionTabs(nimgui);
                renderActions(nimgui);
                renderPlayerModifier(nimgui);
            } else {
                // TODO display selected action
            }
        }

        nimgui.endWindow();
    }

    private void renderActionTabs(NimGui nimgui) {
        final Action.Type[] types = Action.Type.values();
        for (int i = 0; i < types.length; i++) {
            if (i % 3 == 0) nimgui.horizontalBlock();
            if (nimgui.button(types[i].pretty())) {
                currActionTab = types[i];
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

    private void renderActions(NimGui nimgui) {
        final Collection<Action> stubs = ActionManager.INSTANCE.stubs().get(currActionTab);
        // Пробел между категориями и экшнами
        nimgui.horizontalBlock();
        battleWindow.consume(0, battleWindow.nextElemY(), 0, 2);

        nimgui.horizontalBlock();
        for (Action action : stubs) {
            if (nimgui.button(action.pretty())) {
                CrabsNetwork.INSTANCE.sendToServer(new FighterMessage(action.name));
            }
        }
    }

    private void renderPlayerModifier(NimGui nimgui) {

    }

    private void toggleHud() {
        shouldDisplay = !shouldDisplay;

        // Display empty screen with cursor
        final Minecraft mc = Minecraft.getMinecraft();
        if (shouldDisplay) mc.displayGuiScreen(EmptyGuiScreen.INSTANCE);
        else if (mc.currentScreen == EmptyGuiScreen.INSTANCE) mc.displayGuiScreen(null);
    }
}
