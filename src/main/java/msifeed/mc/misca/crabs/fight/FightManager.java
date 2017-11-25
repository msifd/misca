package msifeed.mc.misca.crabs.fight;

import net.minecraft.entity.EntityLivingBase;

import msifeed.mc.misca.crabs.fight.FightException.Type;
import msifeed.mc.misca.crabs.fight.FightNetman.Scope;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public enum FightManager {
    INSTANCE;

    private Logger logger = LogManager.getLogger("Crabs.Fight");
    private HashMap<EntityLivingBase, Fight> entityToFight = new HashMap<>();

    public void createFight(EntityPlayer creator) throws FightException {
        FightMember leader = new FightMember(creator);
        Fight fight = new Fight(creator.getEntityWorld(), leader);
        addMemberToFight(leader, fight);
        FightNetman.INSTANCE.notifyFightMembers(fight, Scope.ALL);
        logger.debug("{} created new fight {}.", leader.name, fight.hashCode());
    }

    public void destroyFight(Fight fight) throws FightException {
        for (FightMember m : fight.members) entityToFight.remove(m.entity);
        FightNetman.INSTANCE.notifyFightMembers(fight, Scope.ALL);
        logger.debug("Fight {} with {} members was destroyed.", fight.hashCode(), fight.members.size());
        fight.members.clear();
    }

    public void addEntityToFight(EntityLivingBase entity, Fight fight) throws FightException {
        if (entityToFight.containsKey(entity)) throw new FightException(Type.ALREADY_IN_FIGHT);
        FightMember member = new FightMember(entity);
        addMemberToFight(member, fight);
        FightNetman.INSTANCE.notifyFightMembers(fight, Scope.MEMBERS);
        logger.debug("Add {} to the fight {}.", member.name, fight.hashCode());
    }

    public void removeEntityFromFight(EntityLivingBase entity, Fight fight) throws FightException {
        if (!entityToFight.containsKey(entity)) throw new FightException(Type.NOT_IN_FIGHT);
        entityToFight.remove(entity);
        fight.members.removeIf(m -> m.entity == entity);
        FightNetman.INSTANCE.notifyFightMembers(fight, Scope.MEMBERS);
        logger.debug("Remove {} from the fight {}.", entity.getCommandSenderName(), fight.hashCode());
    }

    private void addMemberToFight(FightMember member, Fight fight) {
        fight.members.add(member);
        entityToFight.put(member.entity, fight);
    }
}
