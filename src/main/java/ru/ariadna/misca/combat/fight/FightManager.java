package ru.ariadna.misca.combat.fight;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
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

    private Encounter getEncounter(EntityPlayer player) {
        return encounters.get(player.getDisplayName().toLowerCase());
    }

    public void startFight(Lobby lobby) {
        Encounter enc = new Encounter();
        Map<Fighter, Integer> inits = new HashMap<>();

        for (EntityPlayer ep : lobby.getFighters()) {
            Character ch = characterProvider.get(ep.getDisplayName());
            CalcResult init_res = calculon.calculateSystem(ch, Action.INIT);
            Fighter fr = new Fighter(ep, ch);

            enc.fighters.add(fr);
            inits.put(fr, init_res.getResult());
        }

        // Вау магия джавы 8! Сортируем бойцов по инициативе
        enc.fighters.sort(Comparator.comparingInt(inits::get));

        for (EntityPlayer ep : lobby.getFighters()) {
            encounters.put(ep.getDisplayName().toLowerCase(), enc);
        }

        lobby.fightStarted();

        prepareTurn(enc);
    }

    public void stopFight(Lobby lobby) {
        for (EntityPlayer pl : lobby.getFighters()) {
            String name = pl.getDisplayName().toLowerCase();
            fighters.remove(name);
            encounters.remove(name);
        }
    }

    public void selectTarget(EntityPlayer player, String target) {
        Fighter fighter = getFighter(player);
        if (fighter == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.cmb.error.no_fight"));
            return;
        }
        if (fighter.stage == Action.Stage.DEFENCE) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.cmb.error.defence_target"));
            return;
        }
        Fighter target_fighter = fighters.get(target.toLowerCase());
        if (target_fighter == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.cmb.error.player_not_in_fight"));
            return;
        }
        fighter.target = target_fighter;
        player.addChatMessage(new ChatComponentTranslation("misca.combat.cmb.target"));
    }

    public void passTurn(EntityPlayer player) {
        Encounter enc = getEncounter(player);
        if (enc == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.cmb.error.no_fight"));
            return;
        }
        player.addChatMessage(new ChatComponentTranslation("misca.combat.cmb.pass"));
        prepareTurn(enc);
    }

    public void doAction(EntityPlayer player, Action action, int mod) throws CombatException {
        Fighter fighter = getFighter(player);
        if (fighter == null) {
            throw new CombatException(CombatException.Type.NO_FIGHT);
        }
        Encounter enc = getEncounter(player);
        if ((enc.stage == Action.Stage.ATTACK && enc.attacker != fighter) || (enc.stage == Action.Stage.DEFENCE && enc.defendant != fighter)) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.cmb.error.not_your_turn"));
            return;
        }
        // Игнорим не атаку/защиту
        if (action.isSystem()) {
            return;
        }
        if (fighter.stage != action.stage) {
            throw new CombatException(CombatException.Type.WRONG_STAGE);
        }
        if (fighter.target == null || fighter.target.isDead) {
            throw new CombatException(CombatException.Type.NO_TARGET);
        }

        CalcResult result = calculon.calculate(fighter, action, mod);

        LanguageRegistry reg = LanguageRegistry.instance();
        String template = reg.getStringLocalization("misca.combat.cmb.action");
        String act_name = reg.getStringLocalization("misca.combat.cmb.action." + action.toString());
        String player_mod = mod != 0 ? Integer.toString(mod) : "";
        String response = String.format(template, player.getDisplayName(), act_name, player_mod, result.toString());
        player.addChatMessage(new ChatComponentText(response));

        finishTurn(enc);
    }

    private void prepareTurn(Encounter enc) {
        Fighter fr;
        String notify_template;
        if (enc.stage == Action.Stage.ATTACK) {
            enc.attacker = fr = enc.fighters.peek();
            notify_template = LanguageRegistry.instance().getStringLocalization("misca.combat.fight.attack");
        } else {
            enc.defendant = fr = enc.attacker.target;
            enc.defendant.target = enc.attacker;
            notify_template = LanguageRegistry.instance().getStringLocalization("misca.combat.fight.defence");
        }
        fr.stage = enc.stage;
        enc.notifyAll(String.format(notify_template, fr.player.getDisplayName()));
    }

    private void finishTurn(Encounter enc) {
        if (enc.stage == Action.Stage.ATTACK) {
            enc.stage = Action.Stage.DEFENCE;
        } else {
            judgeTurn(enc);
            enc.stage = Action.Stage.ATTACK;
        }
        prepareTurn(enc);
    }

    private void judgeTurn(Encounter enc) {
        int comp = enc.attack_roll.compareTo(enc.defence_roll);
        switch (comp) {
            case 1:
                break;
            case -1:
                break;
            case 0:
                break;
        }
    }
}
