package ru.ariadna.misca.combat.lobby;

import com.google.common.base.Joiner;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import ru.ariadna.misca.combat.fight.FightManager;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyManager {
    private final FightManager fightManager;
    private HashMap<String, Lobby> playerLobbies = new HashMap<>();

    public LobbyManager(FightManager fightManager) {
        this.fightManager = fightManager;
    }

    public void createLobby(EntityPlayer player) {
        String name = player.getDisplayName().toLowerCase();
        if (playerLobbies.containsKey(name)) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.you_in_lobby"));
            return;
        }
        playerLobbies.put(name, new Lobby(player));
        player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.created"));
    }

    public void leaveLobby(EntityPlayer player) {
        String name = player.getDisplayName().toLowerCase();
        if (!playerLobbies.containsKey(name)) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.you_not_in_lobby"));
            return;
        }
        removePlayerFromLobby(player);
    }

    public void joinLobby(EntityPlayer player, String target) {
        if (player.getDisplayName().equalsIgnoreCase(target)) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.join_yourself"));
            return;
        }
        Lobby target_lobby = playerLobbies.get(target.toLowerCase());
        if (target_lobby == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.player_not_in_lobby"));
            return;
        }
        if (target_lobby.fightStarted) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.join_in_fight"));
            return;
        }

        removePlayerFromLobby(player);
        target_lobby.fighters.add(player);
        playerLobbies.put(player.getDisplayName().toLowerCase(), target_lobby);
        player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.joined"));
    }

    public void listLobby(EntityPlayer player) {
        Lobby lobby = playerLobbies.get(player.getDisplayName().toLowerCase());
        if (lobby == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.you_not_in_lobby"));
            return;
        }

        String template = LanguageRegistry.instance().getStringLocalization("misca.combat.lobby.list");
        List<String> names = lobby.fighters.stream().map(EntityPlayer::getDisplayName).collect(Collectors.toList());
        String players = Joiner.on(", ").join(names);
        player.addChatMessage(new ChatComponentText(String.format(template, players)));
    }

    public void startFight(EntityPlayer player) {
        Lobby lobby = playerLobbies.get(player.getDisplayName().toLowerCase());
        if (lobby == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.you_not_in_lobby"));
            return;
        }
        if (lobby.owner != player) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.not_an_owner"));
            return;
        }
        if (lobby.fighters.size() < 2) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.empty_lobby"));
            return;
        }

        for (EntityPlayer pl : lobby.fighters) {
            pl.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.start"));
        }
        fightManager.startFight(lobby);
    }

    public void stopFight(EntityPlayer player) {
        Lobby lobby = playerLobbies.get(player.getDisplayName().toLowerCase());
        if (lobby == null) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.you_not_in_lobby"));
            return;
        }
        if (lobby.owner != player) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.not_an_owner"));
            return;
        }
        if (!lobby.fightStarted) {
            player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.error.not_fighting"));
            return;
        }

        fightManager.stopFight(lobby);
        for (EntityPlayer pl : lobby.fighters) {
            playerLobbies.remove(pl.getDisplayName().toLowerCase());
            pl.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.stop"));
        }
    }

    private void removePlayerFromLobby(EntityPlayer player) {
        Lobby lobby = playerLobbies.get(player.getDisplayName().toLowerCase());
        if (lobby == null) {
            return;
        }

        lobby.fighters.remove(player);
        playerLobbies.remove(player.getDisplayName().toLowerCase());

        // Устанавливаем нового владельца лобби
        if (lobby.owner == player && !lobby.fighters.isEmpty()) {
            lobby.owner = lobby.fighters.getFirst();
            lobby.owner.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.new_owner"));
        }

        player.addChatMessage(new ChatComponentTranslation("misca.combat.lobby.leaved"));
    }
}
