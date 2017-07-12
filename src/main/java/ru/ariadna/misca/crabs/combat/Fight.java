package ru.ariadna.misca.crabs.combat;

import ru.ariadna.misca.crabs.combat.parts.Move;
import ru.ariadna.misca.crabs.lobby.Lobby;

import java.util.LinkedList;

public class Fight {
    Lobby lobby;
    LinkedList<Fighter> queue = new LinkedList<>();
    LinkedList<Move> moves = new LinkedList<>();
    boolean master_op = false;
    boolean skip_master_move = false;

    Fight(Lobby lobby) {
        this.lobby = lobby;
        this.queue.addAll(lobby.members());
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
}
