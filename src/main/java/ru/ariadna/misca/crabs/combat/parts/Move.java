package ru.ariadna.misca.crabs.combat.parts;

import ru.ariadna.misca.crabs.combat.Fighter;

/**
 * Ход обоих игроков. Хранит как действия, так и результаты.
 */
public class Move {
    public Fighter attacker, defender;
    public Action attack, defence;
}
