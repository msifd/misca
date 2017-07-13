package ru.ariadna.misca.crabs.lobby;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.combat.Fighter;
import ru.ariadna.misca.crabs.combat.FighterManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LobbyManager {
    private Map<EntityLivingBase, Lobby> entityToLobby = new HashMap<>();

    public void onInit() {
        ItemBattleFlag itemBattleFlag = new ItemBattleFlag();
        MinecraftForge.EVENT_BUS.register(itemBattleFlag);
        GameRegistry.registerItem(itemBattleFlag, "battle_flag");
    }

    void createLobby(EntityPlayerMP player) {
        if (entityToLobby.containsKey(player)) {
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.you_are_already_in_lobby"));
        }

        Fighter fighter = FighterManager.makeFighter(player);
        if (fighter != null) {
            Lobby lobby = new Lobby(fighter);
            entityToLobby.put(player, lobby);
            Crabs.logger.trace("Created new lobby for {}", player.getDisplayName());
            notifyLobby(lobby);
        }
    }

    void joinToPlayer(EntityPlayerMP player, String targetName) {
        if (entityToLobby.containsKey(player)) {
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.you_are_already_in_lobby"));
            return;
        }

        EntityPlayer host = player.getEntityWorld().getPlayerEntityByName(targetName);
//        EntityPlayer target = MinecraftServer.getServer().getEntityWorld().getPlayerEntityByName(targetName);
        if (host == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.lobby_not_found"));
            return;
        }

        includeToPlayersLobby((EntityPlayerMP) host, player);
    }

    void leaveLobby(EntityPlayerMP player) {
        Lobby lobby = entityToLobby.get(player);
        if (lobby == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.lobby_not_found"));
            return;
        }

        Fighter fighter = lobby.findFighter(player);
        if (!fighter.canLeaveLobby()) {
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.you_cant_leave"));
            return;
        }

        lobby.leave(player);
        entityToLobby.remove(player);

        // Check if players left
        if (lobby.members().stream().noneMatch(f -> f.entity() instanceof EntityPlayer)) {
            closeLobby(lobby);
        }

        Crabs.logger.trace("{} left lobby of {}", player, lobby.master());
        updatePlayersLobby(player);
        notifyLobby(lobby);
        player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.you_left"));
    }

    void includeToPlayersLobby(EntityPlayerMP player, EntityLivingBase target) {
        Lobby lobby = entityToLobby.get(player);
        if (lobby == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.lobby_not_found"));
            return;
        }

        Lobby targets_lobby = entityToLobby.get(target);
        if (targets_lobby != null && targets_lobby != lobby) {
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.fighter_already_in_lobby"));
            return;
        }

        Fighter fighter = FighterManager.makeFighter(target);
        if (fighter != null) {
            lobby.join(fighter);
            entityToLobby.put(target, lobby);

            Crabs.logger.trace("{} joined lobby of {}", target, lobby.master());
            if (target instanceof EntityPlayerMP)
                updatePlayersLobby((EntityPlayerMP) target);
            notifyLobby(lobby);
            if (player != target)
                player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.included"));
            if (target instanceof EntityPlayer)
                ((EntityPlayer) target).addChatMessage(new ChatComponentTranslation("misca.lobby.msg.you_joined"));
        }
    }

    void excludeFromPlayersLobby(EntityPlayerMP player, EntityLivingBase target) {
        Lobby lobby = entityToLobby.get(player);
        if (lobby == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.lobby_not_found"));
            return;
        }

        Lobby targets_lobby = entityToLobby.get(target);
        if (lobby != targets_lobby) return;

        lobby.leave(target);
        entityToLobby.remove(target);

        Crabs.logger.trace("{} left lobby of {}", target, lobby.master());
        if (target instanceof EntityPlayerMP)
            updatePlayersLobby((EntityPlayerMP) target);
        notifyLobby(lobby);

        if (player != target)
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.excluded"));
        if (target instanceof EntityPlayer)
            ((EntityPlayer) target).addChatMessage(new ChatComponentTranslation("misca.lobby.msg.you_left"));
    }

    void startFight(EntityPlayerMP player) {
        Lobby lobby = entityToLobby.get(player);
        if (lobby == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.lobby_not_found"));
            return;
        }
        if (lobby.master() != player && !MiscaUtils.isOp(player)) {
            player.addChatMessage(new ChatComponentTranslation("misca.lobby.msg.you_dont_have_perm"));
            return;
        }

        Crabs.instance.fighterManager.startFight(player, lobby);
    }

    public void closeLobby(Lobby lobby) {
        for (Fighter f : lobby.members()) entityToLobby.remove(f.entity());
        notifyLobby(lobby);
    }

    void updatePlayersLobby(EntityPlayerMP player) {
        Lobby lobby = entityToLobby.get(player);
        LobbyUpdateMessage msg = new LobbyUpdateMessage(lobby);
        Crabs.instance.network.sendTo(msg, player);
    }

    private static void notifyLobby(Lobby lobby) {
        LobbyUpdateMessage msg = new LobbyUpdateMessage(lobby);
        for (Fighter f : lobby.members())
            if (f.entity() instanceof EntityPlayerMP)
                Crabs.instance.network.sendTo(msg, (EntityPlayerMP) f.entity());
    }
}
