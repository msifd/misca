package ru.ariadna.misca.crabs.combat;

import ru.ariadna.misca.crabs.combat.parts.Action;
import ru.ariadna.misca.crabs.combat.parts.Move;
import ru.ariadna.misca.crabs.lobby.Lobby;

import java.util.Collections;
import java.util.LinkedList;

public class Fight {
    Lobby lobby;
    LinkedList<Fighter> queue = new LinkedList<>();
    LinkedList<Move> moves = new LinkedList<>();
    Move current_move;
    boolean skip_master = false;

    Fight(Lobby lobby) {
        this.lobby = lobby;
        this.queue.addAll(lobby.members());
    }

    void start() {
        Collections.shuffle(queue);
        current_move = new Move();
        current_move.attacker = queue.peekFirst();
        current_move.attack = new Action();
    }

    public Lobby lobby() {
        return lobby;
    }

    public LinkedList<Fighter> queue() {
        return queue;
    }

    public LinkedList<Move> moves() {
        return moves;
    }

    public Move current_move() {
        return current_move;
    }

    public Fighter current_fighter() {
        return isAttack() ? current_move.attacker : current_move.defender;
    }

    public Action current_action() { return isAttack() ? current_move.attack : current_move.defence; }

    public boolean isAttack() {
        return current_move.defence == null;
    }

    public boolean isDefence() {
        return current_move.defence != null;
    }
}
