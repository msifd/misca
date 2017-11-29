package msifeed.mc.misca.crabs.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.im.ImGui;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.crabs.battle.FighterContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class CrabsRenderHandler extends Gui {
    public static final CrabsRenderHandler INSTANCE = new CrabsRenderHandler();

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.thePlayer;
        final BattleManager bm = BattleManager.INSTANCE;
        final FighterContext context = bm.getContext(player.getUniqueID());

        final boolean inBattle = context != null;

        ImGui imgui = ImGui.INSTANCE;
        imgui.newFrame();
        if (imgui.button(inBattle ? "Stop fight" : "Start fight", 5, 5)) {
            if (inBattle) bm.leaveBattle(player.getUniqueID());
            else bm.joinBattle(player);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String contextStr = gson.toJson(context);

        String debugInfo = String.format("inBattle: %b\ncontext: %s", inBattle, contextStr);
        imgui.labelMultiline(debugInfo, 120, 5);
    }
}
