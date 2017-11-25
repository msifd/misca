package msifeed.mc.misca.crabs.fight;

import net.minecraft.entity.EntityLivingBase;

import java.util.UUID;

public class FightMember {
    String name;
    UUID uuid;
    transient EntityLivingBase entity;

    FightMember(EntityLivingBase entity) {
        this.name = entity.getCommandSenderName();
        this.uuid = entity.getUniqueID();
        this.entity = entity;
    }
}
