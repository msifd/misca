package ru.ariadna.misca.crabs.lobby;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import ru.ariadna.misca.crabs.combat.Fighter;

import java.util.LinkedList;
import java.util.Optional;

public class Lobby {
    private EntityPlayer master;
    private LinkedList<Fighter> members = new LinkedList<>();

    public Lobby(Fighter master) {
        this.master = (EntityPlayer) master.entity();
        members.add(master);
    }

    void join(Fighter c) {
        members.add(c);
    }

    void leave(EntityLivingBase entity) {
        Optional<Fighter> opt = members.stream().filter(f -> f.entity() == entity).findFirst();
        if (!opt.isPresent()) return;

        members.remove(opt.get());
        if (entity == master && !members.isEmpty()) {
            members.stream()
                    .filter(f -> f.entity() instanceof EntityPlayer)
                    .map(f -> (EntityPlayer) f.entity())
                    .findFirst()
                    .ifPresent(e -> master = e);
        }
    }

    public EntityPlayer master() {
        return master;
    }

    public LinkedList<Fighter> members() {
        return members;
    }

    public Fighter findFighter(EntityLivingBase entity) {
        for (Fighter f : members) {
            if (f.entity() == entity)
                return f;
        }
        return null;
    }

    public void setMaster(EntityPlayer master) {
        this.master = master;
    }

    public void setMembers(LinkedList<Fighter> members) {
        this.members = members;
    }
}
