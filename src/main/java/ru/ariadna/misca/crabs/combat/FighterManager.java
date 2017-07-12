package ru.ariadna.misca.crabs.combat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.combat.parts.Action;
import ru.ariadna.misca.crabs.lobby.Lobby;

import java.util.HashMap;
import java.util.Map;

public class FighterManager {
    private Map<EntityLivingBase, Fight> entityToFight = new HashMap<>();

    private static void notifyLobby(EntityLivingBase source, Fight fight) {
        CombatUpdateMessage msg = new CombatUpdateMessage(fight);

        fight.lobby.members().stream()
                .map(Fighter::entity)
                .filter(e -> e instanceof EntityPlayerMP)
                .forEach(p -> Crabs.instance.network.sendTo(msg, (EntityPlayerMP) p));
    }

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

    public static boolean playerHaveControl(Fight fight, EntityPlayer player, EntityLivingBase entity) {
        return player == entity || (fight.lobby.master() == player && fight.queue.getFirst().entity() == entity);
    }

    public void onInit() {
    }

    public void startFight(EntityPlayerMP player, Lobby lobby) {
        Fight fight = new Fight(lobby);
        for (Fighter f : lobby.members()) {
            entityToFight.put(f.entity(), fight);
        }
        notifyFight(fight);
    }

    public boolean isInFight(EntityLivingBase entity) {
        return entityToFight.containsKey(entity);
    }

    void makeMove(EntityPlayerMP player, EntityLivingBase entity, Action action) {
        Fight fight = entityToFight.get(entity);

        if (fight == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.fight.msg.not_in_fight"));
            return;
        }

        if (player != entity)
            Crabs.logger.info("{} controls {} and makes move {}", player.getCommandSenderName(), entity.getCommandSenderName(), action.type);
        else
            Crabs.logger.info("{} makes move {}", entity.getCommandSenderName(), action.type);

        Fighter fighter = fight.queue.pop();
        fight.queue.addLast(fighter);
        notifyFight(fight);
    }

    private void notifyFight(Fight fight) {
        CombatUpdateMessage message = new CombatUpdateMessage(fight);
        for (Fighter f : fight.lobby.members())
            if (f.entity() instanceof EntityPlayerMP)
                Crabs.instance.network.sendTo(message, (EntityPlayerMP) f.entity());
    }

    void updatePlayersFight(EntityPlayerMP player) {
        Fight fight = entityToFight.get(player);
        CombatUpdateMessage message = new CombatUpdateMessage(fight);
        Crabs.instance.network.sendTo(message, player);
    }
}
