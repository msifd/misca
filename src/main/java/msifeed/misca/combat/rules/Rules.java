package msifeed.misca.combat.rules;

import msifeed.misca.combat.CharAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * You know the rules and so do I
 */
public class Rules {

    // Damage

    public float neutralDamageFactor = 0;
    public float neutralFireDamageFactor = -0.5f;

    public float neutralDamageFactor(DamageSource source) {
        return Math.max(0, 1 + neutralDamageFactor + (source.isFireDamage() ? neutralFireDamageFactor : 0));
    }

    public double damageIncreaseMeleePerStr = 0.04;
    public double damageIncreaseRangePerStr = 0.02;
    public double damageAbsorptionPerEnd = 0.02;

    public float damageFactor(EntityLivingBase actor, EntityLivingBase victim, WeaponInfo weapon) {
        final double strFactor = weapon.traits.contains(WeaponTrait.range)
                ? damageIncreaseRangePerStr
                : damageIncreaseMeleePerStr;
        final float increase = (float) (CharAttribute.str.get(actor) * strFactor);
        final float absorption = (float) (CharAttribute.end.get(victim) * damageAbsorptionPerEnd);
        return Math.max(0, 1 + increase - absorption);
    }

    public double magicCloseRangeDistance = 2;
    public double magicCloseRangeSpreadChance = 0.25;
    public double magicCloseRangeMissChance = 0.25;

    public boolean isCloseRangeMagic(CombatantInfo actor, EntityLivingBase victim) {
        if (!actor.is(WeaponTrait.magic)) return false;
        return actor.entity.getDistance(victim) <= magicCloseRangeDistance;
    }

    // Chances

    public double hitChanceBase = 0.5;
    public double hitChanceMeleePerPer = 0.015;
    public double hitChanceRangePerPer = 0.03;
    public double hitChancePerLck = 0.005;

    public double hitChance(CombatantInfo actor, CombatantInfo victim) {
        final double overrideChance = actor.weapon.chance;
        final double perFactor = actor.isRanged() ? hitChanceRangePerPer : hitChanceMeleePerPer;
        final double perception = CharAttribute.per.get(actor);
        final double luck = CharAttribute.lck.get(actor);
        final double correction = hitChanceCorrection(actor, victim);
        return hitChanceBase + perception * perFactor + luck * hitChancePerLck + correction + overrideChance;
    }

    public double hitChanceRangeCorrectionApThreshold = 5;
    public double hitChanceRangeCorrectionPerAp = 0.03;
    public double hitChanceRangeCorrectionDistanceThreshold = 5;

    public double hitChanceCorrection(CombatantInfo actor, CombatantInfo victim) {
        if (actor.isRanged()) {
            final double ap = attackActionPoints(actor.entity, actor.weapon);
            final double apBonus = Math.max(0, ap - hitChanceRangeCorrectionApThreshold) * hitChanceRangeCorrectionPerAp;
            final double distance = actor.entity.getDistance(victim.entity);
            final double distancePenalty = Math.max(0, distance - hitChanceRangeCorrectionDistanceThreshold - ap) / 100;
            return apBonus + distancePenalty;
        }

        return 0;
    }

    public double evasionChanceBase = 0.25;
    public double evasionChancePerRef = 0.03;
    public double evasionChancePerLck = 0.005;

    public double evasionChance(CombatantInfo actor, CombatantInfo victim) {
        final double reflexes = CharAttribute.ref.get(victim);
        final double luck = CharAttribute.lck.get(victim);
        final double correction = evasionCorrection(actor, victim);
        final double factor = evasionFactor(actor, victim);
        return evasionChanceBase + (reflexes * evasionChancePerRef + luck * evasionChancePerLck + correction) * factor;
    }

    public double evasionMeleeShieldBonus = 0.25;
    public double evasionMeleeWithRangedPenalty = 0.2;

    public double evasionCorrection(CombatantInfo actor, CombatantInfo victim) {
        if (actor.isMelee()) {
            if (victim.is(WeaponTrait.evadeMelee))
                return evasionMeleeShieldBonus;
            else if (victim.isRanged())
                return evasionMeleeWithRangedPenalty;
        }

        return 0;
    }

    public double coverPerBlock = 0.5;

    public double evasionFactor(CombatantInfo actor, CombatantInfo victim) {
        if (actor.isRanged()) {
            double cover = victim.is(WeaponTrait.evadeRange) ? coverPerBlock : 0;
            cover += coverBlocks(victim.entity.world, victim.pos, actor.pos);
            return MathHelper.clamp(cover, 0, 1);
        }

        return 1;
    }

    public double coverBlocks(World world, Vec3d vPos, Vec3d sPos) {
        final Vec3d vec = sPos.subtract(vPos);

        final EnumFacing facingX = EnumFacing.getFacingFromVector((float) vec.x, 0, 0);
        final EnumFacing facingZ = EnumFacing.getFacingFromVector(0, 0, (float) vec.z);
        final BlockPos posX = new BlockPos(vPos).offset(facingX);
        final BlockPos posZ = new BlockPos(vPos).offset(facingZ);

        final double coverage = blockCoverage(world, posX) + blockCoverage(world, posX.up())
                + blockCoverage(world, posZ) + blockCoverage(world, posZ.up());

        return coverage * coverPerBlock;
    }

    private static int blockCoverage(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().isPassable(world, pos) ? 0 : 1;
    }

    // Final hit chance and criticality

    public double maxHitChance = 0.8;
    public double criticalityPerChance = 0.1;

    public double rawChanceToHitCriticality(double chance) {
        return Math.max(0, maxHitChance - chance) * criticalityPerChance;
    }

    public double rawChanceToEvadeCriticality(double chance) {
        return (chance < 0 ? -chance : 0) * criticalityPerChance;
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
        final double speedMultiplier = attackSpeed != null
                ? attackSpeed.getBaseValue() / attackSpeed.getAttributeValue() * attackApSpeedFactor
                : 1;
        final double cost = weapon.forceAtk || weapon.atk > 0 ? weapon.atk : attackApBase;
        return cost * speedMultiplier;
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
