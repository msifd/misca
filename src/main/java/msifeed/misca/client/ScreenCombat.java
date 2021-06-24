package msifeed.misca.client;

import msifeed.mellow.MellowScreen;
import msifeed.mellow.utils.Direction;
import msifeed.mellow.utils.UiBuilder;
import msifeed.mellow.view.button.ButtonLabel;
import msifeed.mellow.view.text.LabelTr;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.client.GuiCombatOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ScreenCombat extends MellowScreen {
    private final EntityPlayer player = Minecraft.getMinecraft().player;

    public ScreenCombat() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void closeGui() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public void initGui() {
        super.initGui();

        final ICombatant com = CombatantProvider.get(player);
        final Battle battle = BattleStateClient.STATE;

        final boolean inBattle = com.isInBattle();
        final boolean canStart = inBattle && !battle.isStarted() && battle.getMembers().size() > 1;
        final boolean started = inBattle && battle.isStarted();
        final boolean inQueue = battle.isInQueue(player.getUniqueID());
        final boolean notInQueue = started && !inQueue;

        UiBuilder.of(container)
                .add(new LabelTr("gui.misca.combat")).center(Direction.HORIZONTAL)

                .beginGroup() // Content

                .beginGroup()
                    .when(started, ui -> ui.add(makeButton("finishTurn", "next")).below())
                    .moveGroup(0, 10, 0)
                    .appendGroup()

                .beginGroup()
                    .when(inQueue, ui -> ui.add(makeButton("leaveQueue", "leave")).below())
                    .when(notInQueue, ui -> ui.add(makeButton("joinQueue", "join")).below())
                    .when(!inBattle, ui -> ui.add(makeButton("init", "init")).below())
                    .when(!inBattle, ui -> ui.add(makeButton("train", "init train")).below())
                    .when(canStart, ui -> ui.add(makeButton("start", "start")).below())
                    .when(inBattle, ui -> ui.add(makeButton("add", "add")).below())
                    .moveGroup(0, 10, 0)
                    .appendGroup()

                .beginGroup()
                    .when(inBattle, ui -> ui.add(makeButton("exit", "exit")).below())
                    .when(inBattle, ui -> ui.add(makeButton("destroy", "destroy")).below())
                    .moveGroup(0, 10, 0)
                    .appendGroup()

                .beginGroup()
                    .when(inBattle, ui -> {
                        final ButtonLabel btn = new ButtonLabel("Debug");
                        btn.setSize(50, 15);
                        btn.setCallback(() -> GuiCombatOverlay.COMBAT_DEBUG = !GuiCombatOverlay.COMBAT_DEBUG);
                        ui.add(btn).below().move(25, 0, 0);
                    })
                    .moveGroup(0, 20, 0)
                    .appendGroup()

                .centerGroup(Direction.HORIZONTAL)
                .appendGroup() // Content

                .centerGroup(Direction.BOTH)
                .moveGroup(80, -10, 0)
                .build();
    }

    private ButtonLabel makeButton(String key, String cmd) {
        final ButtonLabel btn = new ButtonLabel(I18n.format("gui.misca.combat." + key));
        btn.setSize(100, 15);
        btn.setCallback(() -> sendChatMessage("/combat " + cmd, false));
        return btn;
    }

    @SubscribeEvent
    public void onCombatUpdate(BattleStateClient.CombatUpdateEvent event) {
        initGui();
    }
}
