package ru.ariadna.misca.combat.fight;

import net.minecraft.util.ChatComponentText;
import ru.ariadna.misca.combat.calculation.CalcResult;

import java.util.LinkedList;

class Encounter {
    LinkedList<Fighter> fighters = new LinkedList<>();

    Action.Stage stage = Action.Stage.ATTACK;
    Fighter attacker;
    Fighter defendant;

    CalcResult attack_roll;
    CalcResult defence_roll;

    public void notifyAll(String msg) {
        ChatComponentText comp = new ChatComponentText(msg);
        for (Fighter fr : fighters) {
            fr.player.addChatMessage(comp);
        }
    }
}
