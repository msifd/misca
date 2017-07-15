package ru.ariadna.misca.crabs.combat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.combat.parts.Action;
import ru.ariadna.misca.crabs.combat.parts.Move;
import ru.ariadna.misca.crabs.lobby.Lobby;

import java.util.HashMap;
import java.util.Map;

public class FightManager {
    private Map<EntityLivingBase, Fight> entityToFight = new HashMap<>();

    @SideOnly(Side.CLIENT)
    public static Fighter makeFighterClient(int entityId) {
        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
        if (entity != null && entity instanceof EntityLivingBase) {
            return makeFighter((EntityLivingBase) entity);
        }
        return null;
    }

    public static Fighter makeFighter(EntityLivingBase entity) {
        // TODO Get charsheet
        return new Fighter(null, entity);
    }

    public static boolean playerHasControl(EntityPlayer player, Fight fight) {
        Fighter f = fight.current_fighter();
        return player == f.entity() || (fight.lobby.master() == player && !(f.entity() instanceof EntityPlayer));
    }

    public void onInit() {
    }

    public void startFight(EntityPlayerMP player, Lobby lobby) {
        Fight fight = new Fight(lobby);
        for (Fighter f : lobby.members()) entityToFight.put(f.entity(), fight);
        fight.start();

        notifyFight(fight);
    }

    void endFight(EntityPlayerMP player) {
        Fight fight = entityToFight.get(player);

        if (fight == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.fight.msg.not_in_fight"));
            return;
        }

        closeFight(fight);
    }

    public boolean isInFight(EntityLivingBase entity) {
        return entityToFight.containsKey(entity);
    }

    void updateAction(EntityPlayerMP player, int targetId, Action action) {
        Fight fight = entityToFight.get(player);

        if (fight == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.fight.msg.not_in_fight"));
            return;
        }

        if (!playerHasControl(player, fight)) {
            player.addChatMessage(new ChatComponentTranslation("misca.fight.msg.no_control"));
            return;
        }

        if (fight.isAttack()) {
            fight.current_move.attack = action;
            if (targetId > -1) {
                Fighter target = fight.lobby().findFighter(targetId);
                if (target == null || target == fight.current_fighter())
                    player.addChatMessage(new ChatComponentText("Unknown fighter!"));
                else
                    fight.current_move().defender = target;
            }
        } else {
            fight.current_move.defence = action;
        }
    }


    public void makeMove(EntityPlayerMP player) {
        Fight fight = entityToFight.get(player);

        if (fight == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.fight.msg.not_in_fight"));
            return;
        }

        if (!playerHasControl(player, fight)) {
            player.addChatMessage(new ChatComponentTranslation("misca.fight.msg.no_control"));
            return;
        }

        Fighter fighter = fight.current_fighter();
        EntityLivingBase entity = fighter.entity();
        Action action = fight.current_action();

        if (player != entity)
            Crabs.logger.info("{} controls {} and makes move {}", player.getCommandSenderName(), entity.getCommandSenderName(), action.type);
        else
            Crabs.logger.info("{} makes move {}", entity.getCommandSenderName(), action.type);

        if (fight.isAttack()) {
            fight.current_move().defence = new Action();
        } else {
            fight.queue.addLast(fight.queue.pop());
            fight.moves().addFirst(fight.current_move);
            fight.current_move = new Move();
            fight.current_move.attacker = fight.queue().peekFirst();
            fight.current_move.attack = new Action();
        }

        notifyFight(fight);
    }

    void closeFight(Fight fight) {
        for (Fighter f : fight.lobby.members()) entityToFight.remove(f.entity());
        Crabs.instance.lobbyManager.closeLobby(fight.lobby);

        notifyFight(fight);
    }

    void updatePlayersFight(EntityPlayerMP player) {
        Fight fight = entityToFight.get(player);
        CombatUpdateMessage message = new CombatUpdateMessage(fight);
        Crabs.instance.network.sendTo(message, player);
    }

    private static void notifyFight(Fight fight) {
        CombatUpdateMessage message = new CombatUpdateMessage(fight);
        for (Fighter f : fight.lobby.members())
            if (f.entity() instanceof EntityPlayerMP)
                Crabs.instance.network.sendTo(message, (EntityPlayerMP) f.entity());
    }
}
