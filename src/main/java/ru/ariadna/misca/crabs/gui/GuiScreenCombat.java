package ru.ariadna.misca.crabs.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import ru.ariadna.misca.Misca;
import ru.ariadna.misca.crabs.combat.CombatActionMessage;
import ru.ariadna.misca.crabs.combat.Fight;
import ru.ariadna.misca.crabs.combat.FightManager;
import ru.ariadna.misca.crabs.combat.Fighter;
import ru.ariadna.misca.crabs.combat.parts.Action;
import ru.ariadna.misca.crabs.combat.parts.ActionType;
import ru.ariadna.misca.crabs.combat.parts.BodyPartType;
import ru.ariadna.misca.crabs.combat.parts.Move;

import java.util.LinkedList;

public class GuiScreenCombat extends GuiScreen {
    public static final GuiScreenCombat instance = new GuiScreenCombat();

    private Fight fight = null;
    private GuiButtonExt makeMoveBtn;
    private GuiButtonExt endFightBtn;
    private GuiButtonExt rollActionBtn;
    private GuiButtonExt rollBodyPartBtn;
    private GuiButtonExt rollTargetBtn;

    private Action action = new Action();

    @Override
    public void initGui() {
        Misca.crabs.network.sendToServer(new CombatActionMessage(CombatActionMessage.Type.NOOP));

        makeMoveBtn = new GuiButtonExt(0, 0, 0, 100, 20, "move");
        endFightBtn = new GuiButtonExt(1, 0, 20, 100, 20, "end");
        rollActionBtn = new GuiButtonExt(2, 0, 40, 100, 20, "roll action");
        rollBodyPartBtn = new GuiButtonExt(3, 0, 60, 100, 20, "roll bodypart");
        rollTargetBtn = new GuiButtonExt(4, 0, 80, 100, 20, "roll target");

        onFightUpdate(fight);

        buttonList.add(makeMoveBtn);
        buttonList.add(endFightBtn);
        buttonList.add(rollActionBtn);
        buttonList.add(rollBodyPartBtn);
        buttonList.add(rollTargetBtn);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float tick) {
        super.drawScreen(mouseX, mouseY, tick);

        if (fight == null) {
            this.fontRendererObj.drawString("No fight", 120, 0, 0xFFFFFF);
            return;
        }

        Fighter current_fighter = fight.current_fighter();
        Fighter target = fight.isAttack() ? fight.current_move().defender : fight.current_move().attacker;

        StringBuilder sb = new StringBuilder();
        sb.append("Move: ");
        sb.append(current_fighter.entity().getCommandSenderName());
        sb.append("\nStage: ");
        sb.append(fight.isAttack() ? "Attack" : "Defence");
        sb.append("\nAction: ");
        sb.append(action.type == null ? "None" : action.type);
        sb.append("\nBody part: ");
        sb.append(action.bodyPart == null ? "None" : action.bodyPart);
        sb.append("\nTarget: ");
        sb.append(target == null ? "None" : target.entity().getCommandSenderName());
        this.fontRendererObj.drawSplitString(sb.toString(), 110, 0, 150, 0xFFFFFF);

        sb = new StringBuilder();
        sb.append("Queue:");
        for (Fighter f : fight.queue()) {
            sb.append('\n');
            sb.append(f.entity().getCommandSenderName());
        }
        this.fontRendererObj.drawSplitString(sb.toString(), 260, 0, 100, 0xFFFFFF);

        sb = new StringBuilder();
        sb.append("Moves:");
        for (Move m : fight.moves()) {
            sb.append("\n Atk: ");
            sb.append(m.attacker.entity().getCommandSenderName());
            sb.append(" - ");
            sb.append(m.attack.type);
            sb.append(" - ");
            sb.append(m.attack.bodyPart);
            sb.append("\n Def: ");
            sb.append(m.defender.entity().getCommandSenderName());
            sb.append(" - ");
            sb.append(m.defence.type);
            sb.append(" - ");
            sb.append(m.defence.bodyPart);
            sb.append('\n');
        }
        this.fontRendererObj.drawSplitString(sb.toString(), 360, 0, 200, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                sendMove();
                break;
            case 1:
                Misca.crabs.network.sendToServer(new CombatActionMessage(CombatActionMessage.Type.END));
                break;
            case 2: /// Roll action
                int action_ord = action.type == null ? -1 : action.type.ordinal();
                action_ord++;
                if (action_ord >= ActionType.values().length) action_ord = 0;

                action.type = ActionType.values()[action_ord];
                sendUpdate();
                break;
            case 3: // Roll body part
                int body_ord = action.bodyPart == null ? -1 : action.bodyPart.ordinal();
                body_ord++;
                if (body_ord >= BodyPartType.values().length) body_ord = 0;

                action.bodyPart = BodyPartType.values()[body_ord];
                sendUpdate();
                break;
            case 4: // Roll targetId
                if (fight.isDefence()) break;

                Move current_move = fight.current_move();
                LinkedList<Fighter> queue = fight.queue();
                int target_ord = current_move.defender == null ? -1 : queue.indexOf(current_move.defender);
                while (true) {
                    target_ord++;
                    if (target_ord >= fight.queue().size()) target_ord = 0;

                    Fighter target = queue.get(target_ord);
                    if (target == current_move.attacker) continue;
                    current_move.defender = target;
                    break;
                }
                sendUpdate();
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void sendMove() {
        CombatActionMessage msg = new CombatActionMessage(CombatActionMessage.Type.MOVE);
        Misca.crabs.network.sendToServer(msg);
    }

    private void sendUpdate() {
        CombatActionMessage msg = new CombatActionMessage(CombatActionMessage.Type.UPDATE);
        msg.targetId = fight.current_move().defender == null ? -1 : fight.current_move().defender.entityId();
        msg.action = this.action;
        Misca.crabs.network.sendToServer(msg);
    }

    @SideOnly(Side.CLIENT)
    public void onFightUpdate(Fight fight) {
        this.fight = fight;
        if (fight != null) action = fight.isAttack() ? fight.current_move().attack : fight.current_move().defence;
        else action = new Action();

        if (Minecraft.getMinecraft().currentScreen != instance) return;

        EntityPlayer self = Minecraft.getMinecraft().thePlayer;
        boolean underControl = fight != null && FightManager.playerHasControl(self, fight);

        makeMoveBtn.enabled = underControl;
        endFightBtn.enabled = underControl && fight.lobby().master() == self;
        rollActionBtn.enabled = underControl;
        rollBodyPartBtn.enabled = underControl;
        rollTargetBtn.enabled = underControl && fight.isAttack();
    }
}
