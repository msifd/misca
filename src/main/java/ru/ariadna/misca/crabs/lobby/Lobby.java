package ru.ariadna.misca.crabs.lobby;

import net.minecraft.entity.player.EntityPlayerMP;
import ru.ariadna.misca.crabs.CrabsException;
import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.crabs.combat.Fighter;

import java.util.LinkedList;

public class Lobby {
    Fighter master;
    LinkedList<Fighter> characters = new LinkedList<>();

    Lobby(Fighter master) {
        if (!(master.entity() instanceof EntityPlayerMP))
            throw new CrabsException("Lobby master must be a Player!");

        this.master = master;
        characters.add(master);
    }

    public void join(Fighter c) {
        characters.add(c);
    }

    public void leave(Fighter c) {
        characters.remove(c);
        if (c == master) {
            master = characters.getFirst();
        }
    }

    public Fighter master() {
        return master;
    }

    public LinkedList<Fighter> members() {
        return characters;
    }

    public Character[] membersArray() {
        Character[] arr = new Character[characters.size()];
        characters.toArray(arr);
        return arr;
    }
}
