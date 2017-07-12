package ru.ariadna.misca.crabs.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.combat.CombatActionMessage;
import ru.ariadna.misca.crabs.combat.CombatUpdateMessage;
import ru.ariadna.misca.crabs.combat.Fight;
import ru.ariadna.misca.crabs.combat.Fighter;
import ru.ariadna.misca.crabs.combat.parts.Action;
import ru.ariadna.misca.crabs.combat.parts.ActionType;

public class GuiScreenCombat extends GuiScreen {

    private static Fight fight = null;
    private GuiButtonExt makeMoveBtn;
    private GuiButtonExt skipMoveBtn;
    private GuiButtonExt endFightBtn;
    private Action action = new Action();

    public GuiScreenCombat() {
        Crabs.instance.network.sendToServer(new CombatActionMessage(CombatActionMessage.Type.NOOP));
    }

    @Override
    public void initGui() {
        makeMoveBtn = new GuiButtonExt(0, 0, 0, 100, 20, "move");
        endFightBtn = new GuiButtonExt(1, 0, 20, 100, 20, "end");
        skipMoveBtn = new GuiButtonExt(2, 0, 40, 100, 20, "skip");

        buttonList.add(makeMoveBtn);
        buttonList.add(endFightBtn);
        buttonList.add(skipMoveBtn);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float tick) {
        super.drawScreen(mouseX, mouseY, tick);

        if (fight == null) {
            this.fontRendererObj.drawString("No fight", 120, 0, 0xFFFFFF);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Queue:");
        for (Fighter f : fight.queue()) {
            sb.append('\n');
            sb.append(f.entity().getCommandSenderName());
        }
        this.fontRendererObj.drawSplitString(sb.toString(), 120, 0, 200, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                action.type = ActionType.HEAVY_STRIKE; // TODO remove
                makeMove();
                break;
            case 1:
                // End fight
                break;
            case 2:
                action.type = ActionType.SKIP;
                makeMove();
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void makeMove() {
        CombatActionMessage msg = new CombatActionMessage(CombatActionMessage.Type.MOVE);
        msg.action = this.action;
        Crabs.instance.network.sendToServer(msg);
    }

    public void onCombatUpdate(CombatUpdateMessage msg) {
        fight = msg.fight;

        EntityPlayer self = Minecraft.getMinecraft().thePlayer;

        endFightBtn.enabled = fight != null && fight.lobby().master() == self;
        makeMoveBtn.enabled = fight != null;
    }
}
