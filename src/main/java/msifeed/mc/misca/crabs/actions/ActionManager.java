package msifeed.mc.misca.crabs.actions;

import msifeed.mc.misca.crabs.battle.CrabsDamage;
import msifeed.mc.misca.crabs.battle.FighterContext;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public enum ActionManager {
    INSTANCE;

    private Logger logger = LogManager.getLogger("Crabs.Actions");

    private HashMap<UUID, Move> activeMoves = new HashMap<>();
    private ArrayList<Move> completeMoves = new ArrayList<>();

    /*
        1 атакующий
            выбор действия
            описание действия
            удар по противнику (или не удар, в зависимости от действия, например побега)
                нанесенный урон и тип действия сохраняются
            ожидание действия противника
        2 защищающийся
            выбор действия
            описание действия
            удар по атакующему
        3 итоги
            броски дайсов
            применение эффектов
            закрытие хода
     */

    public void dealDamage(FighterContext actor, FighterContext target, EntityDamageSource damageSource, float amount) {
        // Первый удар
        if (actor.status == FighterContext.Status.ACT) {
            actor.target = target.uuid;
            actor.updateStatus(FighterContext.Status.DEAL_DAMAGE);
            target.target = actor.uuid;
        }

        actor.damageDealt += amount;
    }

    public void stopDealingDamage(FighterContext actor) {
        actor.updateStatus(FighterContext.Status.WAIT);

        Move move = activeMoves.get(actor.uuid);

        if (move == null) { // Боец атаковал
            move = new Move();
            move.attacker = actor;
            activeMoves.put(actor.target, move); // Сверхлогика
        }
        else { // Боец отвечал
            // TODO мультитаргет для остановки побега?
            move.defender = actor;
            completeMoves.add(move);
            activeMoves.remove(actor.uuid);
        }
    }

    public void finalizeMoves() {
        for (Move m : completeMoves) finalizeMove(m.attacker, m.defender);
        completeMoves.clear();
    }

    private void finalizeMove(FighterContext attacker, FighterContext defender) {
        // TODO do
        // TODO плавающее ограничение на получаемый урон чтобы стимулировать нокауты

        final boolean attackSuccessful = attacker.damageDealt >= defender.damageDealt;
        final FighterContext winner = attackSuccessful ? attacker : defender;
        final FighterContext looser = attackSuccessful ? defender : attacker;

        looser.entity.attackEntityFrom(new CrabsDamage(winner.entity), winner.damageDealt);

        attacker.reset();
        defender.reset();
    }

    private static class Move {
        FighterContext attacker, defender;
    }
}
