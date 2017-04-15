package ru.ariadna.misca.combat.lobby;

import com.google.common.base.Joiner;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import ru.ariadna.misca.combat.fight.FightManager;

import java.util.HashMap;

public class LobbyManager {
    private final FightManager fightManager;
    private HashMap<String, Lobby> playerLobbies = new HashMap<>();

    public LobbyManager(FightManager fightManager) {
        this.fightManager = fightManager;
    }

    public void createLobby(EntityPlayer player) {
        String name = player.getDisplayName().toLowerCase();
        if (playerLobbies.containsKey(name)) {
            player.addChatComponentMessage(new ChatComponentTranslation("misca.combat.lobby.error.you_in_lobby"));
        }
        playerLobbies.put(name, new Lobby(player));
        player.addChatComponentMessage(new ChatComponentTranslation("misca.combat.lobby.created"));
    }

    public void leaveLobby(EntityPlayer player) {
        String name = player.getDisplayName().toLowerCase();
        if (playerLobbies.containsKey(name)) {
            player.addChatComponentMessage(new ChatComponentTranslation("misca.combat.lobby.error.you_not_in_lobby"));
        }
        removePlayerFromLobby(player);
        player.addChatComponentMessage(new ChatComponentTranslation("misca.combat.lobby.leaved"));
    }

    public void joinLobby(EntityPlayer player, String target) {
//        if (playerLobbies.containsKey(creator)) {
//            creator.addChatComponentMessage(new ChatComponentTranslation("misca.combat.lobby.error.you_in_lobby"));
//        }
        Lobby target_lobby = playerLobbies.get(target.toLowerCase());
        if (target_lobby == null) {
            player.addChatComponentMessage(new ChatComponentTranslation("misca.combat.lobby.error.player_not_in_lobby"));
            return;
        }

        removePlayerFromLobby(player);
        target_lobby.fighters.add(player);
        playerLobbies.put(player.getDisplayName().toLowerCase(), target_lobby);
        player.addChatComponentMessage(new ChatComponentTranslation("misca.combat.lobby.joined"));
    }

    public void listLobby(EntityPlayer player) {
        Lobby lobby = playerLobbies.get(player.getDisplayName().toLowerCase());
        if (lobby == null) {
            player.addChatComponentMessage(new ChatComponentTranslation("misca.combat.lobby.error.you_not_in_lobby"));
            return;
        }

        String template = LanguageRegistry.instance().getStringLocalization("misca.combat.lobby.list");
        String players = Joiner.on(", ").join(lobby.fighters);
        player.addChatComponentMessage(new ChatComponentText(String.format(template, players)));
    }

    public void fightLobby(EntityPlayer player) {
        Lobby lobby = playerLobbies.get(player.getDisplayName().toLowerCase());
        if (lobby == null) {
            player.addChatComponentMessage(new ChatComponentTranslation("misca.combat.lobby.error.you_not_in_lobby"));
            return;
        }
        if (lobby.owner != player) {
            player.addChatComponentMessage(new ChatComponentTranslation("misca.combat.lobby.error.not_an_owner"));
            return;
        }

        fightManager.startFight(lobby);
    }

    private void removePlayerFromLobby(EntityPlayer player) {
        Lobby lobby = playerLobbies.get(player.getDisplayName().toLowerCase());
        if (lobby == null) {
            return;
        }

        lobby.fighters.remove(player);
        if (lobby.owner == player && !lobby.fighters.isEmpty()) {
            lobby.owner = lobby.fighters.getFirst();
        }
    }
}
