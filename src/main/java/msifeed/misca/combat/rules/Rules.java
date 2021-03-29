package msifeed.misca.combat.rules;

import msifeed.misca.combat.CharAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * You know the rules and so do I
 */
public class Rules {
    public double damageIncreaseMeleePerStr = 0.04;
    public double damageIncreaseRangePerStr = 0.02;

    public float damageIncrease(CombatantInfo info) {
        final double strFactor = info.isMelee() ? damageIncreaseMeleePerStr : damageIncreaseRangePerStr;
        return (float) (CharAttribute.str.get(info) * strFactor);
    }

    public double damageAbsorptionPerEnd = 0.02;

    public float damageAbsorption(CombatantInfo info) {
        return (float) (CharAttribute.end.get(info) * damageAbsorptionPerEnd);
    }

    public double hitChanceBase = 0.5;
    public double hitChanceMeleePerPer = 0.015;
    public double hitChanceRangePerPer = 0.03;
    public double hitChancePerLck = 0.005;

    public double hitChance(CombatantInfo info, WeaponInfo weapon) {
        final double overrideRate = weapon.chance;
        final double perFactor = info.isMelee() ? hitChanceMeleePerPer : hitChanceRangePerPer;
        final double perception = CharAttribute.per.get(info);
        final double luck = CharAttribute.lck.get(info);
        return hitChanceBase + perception * perFactor + luck * hitChancePerLck + overrideRate;
    }

    public double evasionChancePerRef = 0.03;
    public double evasionChancePerLck = 0.005;
    public double rangedEvasionPenalty = 0.2;
    public double noShieldEvasionPenalty = 0.25;

    public double evasionChance(EntityLivingBase victim, CombatantInfo vicInfo, CombatantInfo srcInfo) {
        final double reflexes = CharAttribute.ref.get(vicInfo);
        final double luck = CharAttribute.lck.get(vicInfo);
        final double penalty = evasionPenalty(vicInfo, srcInfo);
        final double factor = evasionFactor(victim, vicInfo, srcInfo);
        return (reflexes * evasionChancePerRef + luck * evasionChancePerLck - penalty) * factor;
    }

    public double evasionPenalty(CombatantInfo vicInfo, CombatantInfo srcInfo) {
        double penalty = 0;

        if (srcInfo.isMelee()) {
            if (vicInfo.isMelee()) {
                if (!vicInfo.isAny(WeaponTrait.evadeMelee)) penalty += noShieldEvasionPenalty;
            } else {
                if (!vicInfo.isAny(WeaponTrait.evadeMelee)) penalty += rangedEvasionPenalty;
            }
        }

        return penalty;
    }

    public double evasionFactor(EntityLivingBase victim, CombatantInfo vicInfo, CombatantInfo srcInfo) {
        if (srcInfo.isRanged()) {
            if (vicInfo.isAny(WeaponTrait.evadeRange))
                return 1;
            return coverBlocks(victim.world, vicInfo.pos, srcInfo.pos);
        } else {
            return 1;
        }
    }

    public double coverPerBlock = 0.5;

    public double coverBlocks(World world, Vec3d vPos, Vec3d sPos) {
        final Vec3d vec = sPos.subtract(vPos);

        final EnumFacing facingX = EnumFacing.getFacingFromVector((float) vec.x, 0, 0);
        final EnumFacing facingZ = EnumFacing.getFacingFromVector(0, 0, (float) vec.z);
        final BlockPos posX = new BlockPos(vPos).offset(facingX);
        final BlockPos posZ = new BlockPos(vPos).offset(facingZ);

        final double coverage = blockCoverage(world, posX) + blockCoverage(world, posX.up())
                + blockCoverage(world, posZ) + blockCoverage(world, posZ.up());

        return Math.min(coverage * coverPerBlock, 1);
    }

    private static int blockCoverage(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().isPassable(world, pos) ? 0 : 1;
    }

    // Final hit chance and criticality

    public double maxHitChance = 0.8;
    public double criticalityConversionRate = 0.1;

    public double rawChanceToHitCriticality(double chance) {
        return Math.max(0, maxHitChance - chance) * criticalityConversionRate;
    }

    public double rawChanceToEvadeCriticality(double chance) {
        return (chance < 0 ? -chance : 0) * criticalityConversionRate;
    }

    public double criticalHitBase = 0.01;
    public double criticalHitPerPer = 0.0025;
    public double criticalHitPerLck = 0.0075;

    public double criticalHit(CombatantInfo info) {
        final double perception = CharAttribute.per.get(info);
        final double luck = CharAttribute.lck.get(info);
        return criticalHitBase + perception * criticalHitPerPer + luck * criticalHitPerLck;
    }

    public double criticalEvasionBase = 0.01;
    public double criticalEvasionPerEnd = 0.0025;
    public double criticalEvasionPerLck = 0.0075;

    public double criticalEvasion(CombatantInfo info) {
        final double endurance = CharAttribute.end.get(info);
        final double luck = CharAttribute.lck.get(info);
        return criticalEvasionBase + endurance * criticalEvasionPerEnd + luck * criticalEvasionPerLck;
    }

    // Action points

    public double attackApBase = 2;
    public double attackApSpeedFactor = 1;

    public double attackActionPoints(EntityLivingBase entity, WeaponInfo weapon) {
        final IAttributeInstance attackSpeed = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED); // Can be null for mobs
        final double speedRate = attackSpeed != null
                ? attackSpeed.getBaseValue() / attackSpeed.getAttributeValue() * attackApSpeedFactor
                : 1;
        final double cost = weapon.atk > 0 ? weapon.atk : attackApBase;
        return cost * speedRate;
    }

    public double movementApFactor = 1;
    public double movementApRiseFactor = 1;
    public double movementApDescentFactor = 0;
    public double movementApSpeedFactor = 1;

    public double movementActionPoints(EntityLivingBase entity, Vec3d from, Vec3d to) {
        final IAttributeInstance speed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        final double speedFactor = speed.getBaseValue() / speed.getAttributeValue() * movementApSpeedFactor;
        final double distance = from.distanceTo(to) * movementApFactor;
        final double verticalDelta = to.y - from.y;

        if (verticalDelta > 0) {
            return (distance + verticalDelta * movementApRiseFactor) * speedFactor;
        } else if (verticalDelta < 0) {
            return (distance - verticalDelta * movementApDescentFactor) * speedFactor;
        } else {
            return distance * speedFactor;
        }
    }

    public double usageApBase = 5;

    public double usageActionPoints(WeaponInfo weapon) {
        final double override = weapon.use;
        return override > 0 ? override : usageApBase;
    }

    public double apPerMoveBase = 5;
    public double apPerMovePerAgi = 1;

    public double actionPointsPerMove(EntityLivingBase entity) {
        return apPerMoveBase + CharAttribute.agi.get(entity) * apPerMovePerAgi;
    }

    public double maxApBase = 0;
    public double maxApPerAgi = 4;

    public double maxActionPoints(EntityLivingBase entity) {
        return maxApBase + CharAttribute.agi.get(entity) * maxApPerAgi;
    }

    // Other

    public int potionTicks = 200;
    public long finishTurnDelayMillis = 1000;
}
