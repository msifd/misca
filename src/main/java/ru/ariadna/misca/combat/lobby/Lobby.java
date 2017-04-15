package ru.ariadna.misca.combat.lobby;

import net.minecraft.entity.player.EntityPlayer;

import java.util.LinkedList;

public class Lobby {
    EntityPlayer owner;
    LinkedList<EntityPlayer> fighters = new LinkedList<>();

    Lobby(EntityPlayer creator) {
        owner = creator;
        fighters.add(creator);
    }
}
