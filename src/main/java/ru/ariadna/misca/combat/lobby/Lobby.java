package ru.ariadna.misca.combat.lobby;

import net.minecraft.entity.player.EntityPlayer;

import java.util.LinkedList;

public class Lobby {
    EntityPlayer owner;
    boolean fightStarted = false;
    LinkedList<EntityPlayer> fighters = new LinkedList<>();

    Lobby(EntityPlayer creator) {
        owner = creator;
        fighters.add(creator);
    }

    public void fightStarted() {
        fightStarted = true;
    }

    public LinkedList<EntityPlayer> getFighters() {
        return fighters;
    }
}
