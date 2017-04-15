package ru.ariadna.misca.combat.fight;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import ru.ariadna.misca.combat.CombatException;
import ru.ariadna.misca.combat.calculation.CalcResult;
import ru.ariadna.misca.combat.calculation.Calculon;
import ru.ariadna.misca.combat.characters.Character;
import ru.ariadna.misca.combat.characters.CharacterProvider;
import ru.ariadna.misca.combat.lobby.Lobby;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FightManager {
    private final CharacterProvider characterProvider;
    private final Calculon calculon;

    private Map<String, Fighter> fighters = new HashMap<>();
    private Map<String, Encounter> encounters = new HashMap<>();

    public FightManager(CharacterProvider characterProvider, Calculon calculon) {
        this.characterProvider = characterProvider;
        this.calculon = calculon;
    }

    public void init() {
    }

    /**
     * @return Бойца или null
     */
    public Fighter getFighter(EntityPlayer player) {
        return fighters.get(player.getDisplayName().toLowerCase());
    }

    public void startFight(Lobby lobby) {
        Encounter enc = new Encounter();
        Map<Fighter, Integer> inits = new HashMap<>();

        for (EntityPlayer ep : lobby.getFighters()) {
            Character ch = characterProvider.get(ep.getDisplayName());
            Fighter fr = new Fighter(ep, ch);
            CalcResult init_res = calculon.calculate(ch, Action.INIT, 0);

            enc.fighters.add(fr);
            inits.put(fr, init_res.getResult());
        }

        // Вау магия джавы 8! Сортируем бойцов по инициативе
        enc.fighters.sort(Comparator.comparingInt(inits::get));

        for (EntityPlayer ep : lobby.getFighters()) {
            encounters.put(ep.getDisplayName().toLowerCase(), enc);
        }

        lobby.fightStarted();
    }

    public void selectTarget(EntityPlayer player, String target) {
        Fighter fighter = getFighter(player);
        if (fighter == null) {
            player.addChatMessage(new ChatComponentText("misca.combat.cmb.error.no_fight"));
            return;
        }
        Fighter target_fighter = fighters.get(target.toLowerCase());
        if (target_fighter == null) {
            player.addChatMessage(new ChatComponentText("misca.combat.cmb.error.player_not_in_fight"));
            return;
        }
        fighter.target = target_fighter;
        player.addChatMessage(new ChatComponentText("misca.combat.cmb.target"));
    }

    public void doAction(EntityPlayer player, Action action, int mod) throws CombatException {
        Fighter fighter = getFighter(player);
        if (fighter == null) {
            throw new CombatException(CombatException.Type.NO_FIGHT);
        }
        // Игнорим не атаку/защиту
        if (action.isSystem()) {
            return;
        }
        if (fighter.stage != action.stage) {
            throw new CombatException(CombatException.Type.WRONG_STAGE);
        }
        if (fighter.target == null || fighter.target.isDead()) {
            throw new CombatException(CombatException.Type.NO_TARGET);
        }

        CalcResult result = calculon.calculate(fighter.character, action, mod);
    }
}
