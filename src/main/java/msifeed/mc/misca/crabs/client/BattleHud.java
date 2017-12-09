package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.imgui.ImGui;
import msifeed.mc.imgui.input.KeyTracker;
import msifeed.mc.imgui.parts.ImWindow;
import msifeed.mc.misca.crabs.actions.Actions;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.crabs.battle.BattleNetwork;
import msifeed.mc.misca.crabs.battle.FighterContext;
import msifeed.mc.misca.crabs.battle.FighterMessage;
import msifeed.mc.misca.crabs.character.Stats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public enum BattleHud {
    INSTANCE;

    private Boolean shouldDisplay = false;
    private ImWindow battleWindow = new ImWindow("Battle", () -> shouldDisplay = !shouldDisplay);

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        if (KeyTracker.isTapped(CrabsKeyBinds.battleHud.getKeyCode()))
            shouldDisplay = !shouldDisplay;

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

        ImGui imgui = ImGui.INSTANCE;
        imgui.newFrame();

        {
            imgui.beginWindow(battleWindow);

            if (imgui.button(inBattle ? "Stop fight" : "Start fight")) {
                FighterMessage message = new FighterMessage(inBattle ? FighterMessage.Type.LEAVE : FighterMessage.Type.JOIN);
                BattleNetwork.INSTANCE.notifyServer(message);
            }

            imgui.label("foobar baby!!!!");

            if (inBattle) {
                if (imgui.button("Punch")) {
                    BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Actions.test_punch));
                }
                if (imgui.button("Fireball")) {
                    BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Actions.test_fireball));
                }
                if (imgui.button("Roll ERP")) {
                    BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Stats.DET));
                }
            }

            imgui.endWindow();
        }

//        if (imgui.button(inBattle ? "Stop fight" : "Start fight", 5, 5)) {
//            FighterMessage message = new FighterMessage(inBattle ? FighterMessage.Type.LEAVE : FighterMessage.Type.JOIN);
//            BattleNetwork.INSTANCE.notifyServer(message);
//        }
//
//        if (inBattle) {
//            if (imgui.button("Punch", 5, 30)) {
//                BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Actions.test_punch));
//            }
//            if (imgui.button("Fireball", 5, 55)) {
//                BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Actions.test_fireball));
//            }
//            if (imgui.button("Roll ERP", 5, 80)) {
//                BattleNetwork.INSTANCE.notifyServer(new FighterMessage(Stats.DET));
//            }
//        }
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String contextStr = gson.toJson(BattleManager.INSTANCE.getContexts());
//
//        String debugInfo = String.format("inBattle: %b\nme: %s\ncontexts: %s", inBattle, player.getUniqueID(), contextStr);
//        imgui.labelMultiline(debugInfo, 120, 5);
    }
}
