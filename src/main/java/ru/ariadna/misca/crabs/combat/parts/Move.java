package ru.ariadna.misca.crabs.combat.parts;

import ru.ariadna.misca.crabs.combat.Fighter;

import java.io.Serializable;

/**
 *
 */
public class Move implements Serializable {
    public Fighter attacker, defender;
    public Action attack, defence;
}
