package ru.ariadna.misca.crabs.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import ru.ariadna.misca.crabs.characters.Character;

public class Fighter {
    private Character character;
    private EntityLivingBase entity;

    private boolean canLeaveLobby = true;
    private boolean hasDescribedMove = false;
    // TODO Эффекты боя

    public Fighter(Character c, EntityLivingBase entity) {
        this.character = c;
        this.entity = entity;
    }

    public Character character() {
        return character;
    }

    public EntityLivingBase entity() {
        return entity;
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

}
