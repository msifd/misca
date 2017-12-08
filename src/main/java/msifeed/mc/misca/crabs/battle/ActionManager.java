package msifeed.mc.misca.crabs.battle;

import msifeed.mc.misca.crabs.EntityUtils;
import msifeed.mc.misca.crabs.rules.ActionResult;
import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Rules;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EntityDamageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

public enum ActionManager {
    INSTANCE;

    private Logger logger = LogManager.getLogger("Crabs.Actions");

    // uuid защищяющегося -> ход атаковавшего
    private HashMap<UUID, Move> pendingMoves = new HashMap<>();
    private ArrayList<Move> completeMoves = new ArrayList<>();

    public void dealDamage(FighterContext actor, FighterContext target, EntityDamageSource damageSource, float amount) {
        // Первый удар
        if (actor.status == FighterContext.Status.ACT) {
            actor.target = target.uuid;
            actor.updateStatus(FighterContext.Status.DEAL_DAMAGE);
            target.target = actor.uuid;
        }

        // TODO ограничение на скорость ударов (при текущей отмене урона они не ограничиваются)
        actor.damageDealt += amount;
    }

    public void stopDealingDamage(FighterContext actor) {
        actor.updateStatus(FighterContext.Status.WAIT);

        Move move = pendingMoves.get(actor.uuid);

        if (move == null) { // Боец атаковал
            move = new Move();
            move.attacker = actor;
            pendingMoves.put(actor.target, move); // Сверхлогика
        } else { // Боец отвечал
            // TODO мультитаргет для остановки побега?
            move.defender = actor;
            completeMoves.add(move);
            pendingMoves.remove(actor.uuid);
        }
    }

    public void finalizeMoves() {
        for (Move m : completeMoves) finalizeMove(m.attacker, m.defender);
        completeMoves.clear();
    }

    private void finalizeMove(FighterContext attacker, FighterContext defender) {
        // TODO плавающее ограничение на получаемый урон чтобы стимулировать нокауты

        final ActionResult attack = new ActionResult(attacker);
        final ActionResult defence = new ActionResult(defender);

        final ActionResult winner = Rules.computeWinner(attack, defence);
        final ActionResult looser = (winner == attack ? defence : attack);

        for (Effect eff : winner.action.target_effects) eff.apply(winner.ctx, looser.ctx);
        for (Effect eff : winner.action.self_effects) eff.apply(winner.ctx, winner.ctx);
        // TODO handle some tags

        notifyMoveResult(winner, looser);

        attacker.reset();
        defender.reset();
    }

    private void notifyMoveResult(ActionResult winner, ActionResult looser) {
        String msg = ActionFormatter.formatActionResults(winner, looser);
        EntityLivingBase we = winner.ctx.entity, le = looser.ctx.entity;
        Stream.concat(
                EntityUtils.getPlayersAround(we, BattleDefines.NOTIFICATION_RADIUS),
                EntityUtils.getPlayersAround(le, BattleDefines.NOTIFICATION_RADIUS)
        )
                .distinct()
                .forEach(player -> player.addChatMessage(new ChatComponentText(msg)));
    }

    private static class Move {
        FighterContext attacker, defender;
    }
}
