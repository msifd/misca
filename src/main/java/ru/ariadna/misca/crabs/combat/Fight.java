package ru.ariadna.misca.crabs.combat;

import java.util.ArrayList;
import java.util.List;

public class Fight {
    Fighter master;
    List<Fighter> fighters = new ArrayList<>();
    List<Move> moves = new ArrayList<>();
    Move current_move;
    boolean skip_master = false;

    Fight(Fighter master, List<Fighter> fighters) {
        this.master = master;
        this.fighters = fighters;
    }

    public Fighter leader() {
        return master;
    }

    public List<Fighter> fighters() {
        return fighters;
    }
}
