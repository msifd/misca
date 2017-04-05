package ru.ariadna.misca.combat;

import ru.ariadna.misca.combat.fight.Fighter;

import java.util.HashMap;
import java.util.Map;

public class CombatManager {
    Map<String, Fighter> fighters = new HashMap<>();

    public void init() {

    }

    public Fighter getFighter(String username) {
        return fighters.get(username);
    }
}
