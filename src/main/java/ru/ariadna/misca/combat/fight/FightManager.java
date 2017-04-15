package ru.ariadna.misca.combat.fight;

import net.minecraft.entity.player.EntityPlayer;
import ru.ariadna.misca.combat.CombatException;
import ru.ariadna.misca.combat.calculation.CalcResult;
import ru.ariadna.misca.combat.calculation.Calculon;
import ru.ariadna.misca.combat.fight.Action;
import ru.ariadna.misca.combat.fight.Fighter;
import ru.ariadna.misca.combat.lobby.Lobby;

import java.util.HashMap;
import java.util.Map;

public class FightManager {
    private final Calculon calculon;

    Map<String, Fighter> fighters = new HashMap<>();

    public FightManager(Calculon calculon) {
        this.calculon = calculon;
    }

    public void init() {

    }

    public Fighter getFighter(String username) {
        return fighters.get(username.toLowerCase());
    }

    public void doAction(EntityPlayer player, Action a, int mod) throws CombatException {
        Action.Stage action_stage = a.stage;

        Fighter fighter = getFighter(player.getDisplayName());
        if (fighter == null && action_stage != Action.Stage.NONE) {
            throw new CombatException();
        }
        if (fighter != null && fighter.stage != action_stage) {
            throw new CombatException();
        }

        CalcResult result = calculon.calculate(fighter.character, a, mod);
    }

    public void startFight(Lobby lobby) {

    }
}
