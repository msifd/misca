package ru.ariadna.misca.crabs.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import ru.ariadna.misca.crabs.characters.Character;

import java.io.Serializable;

public class Fighter implements Serializable {
    private Character character;
    private transient EntityLivingBase entity;
    private int entityId;

    private boolean canLeaveLobby = true;
    private boolean hasDescribedMove = false;
    // TODO Эффекты боя

    public Fighter(Character c, EntityLivingBase entity) {
        this.character = c;
        this.entity = entity;
        this.entityId = entity.getEntityId();
    }

    public Character character() {
        return character;
    }

    public EntityLivingBase entity() {
        return entity;
    }

    public void setEntity(EntityLivingBase entity) {
        this.entity = entity;
        this.entityId = entity.getEntityId();
    }

    public int entityId() {
        return entityId;
    }

    public boolean canLeaveLobby() {
        return canLeaveLobby;
    }

    public void setCanLeaveLobby(boolean canLeaveLobby) {
        this.canLeaveLobby = canLeaveLobby;
    }

    public boolean hasDescribedMove() {
        return !(entity instanceof EntityPlayer) || hasDescribedMove;
    }

    public void setHasDescribedMove(boolean hasDescribedMove) {
        this.hasDescribedMove = hasDescribedMove;
    }

    public void findEntityInWorld(World world) {
        Entity e = world.getEntityByID(entityId);
        if (e instanceof EntityLivingBase)
            this.entity = (EntityLivingBase) e;
    }
}
