package msifeed.misca.combat.rules;

import msifeed.misca.combat.CharAttribute;
import msifeed.misca.combat.Combat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
        final double strFactor = info.is(WeaponTrait.melee) ? damageIncreaseMeleePerStr : damageIncreaseRangePerStr;
        return (float) (CharAttribute.str.get(info) * strFactor);
    }

    public double damageAbsorptionPerEnd = 0.02;

    public float damageAbsorption(CombatantInfo info) {
        return (float) (CharAttribute.end.get(info) * damageAbsorptionPerEnd);
    }

    public double hitRateBase = 0.5;
    public double hitRateMeleePerPer = 0.015;
    public double hitRateRangePerPer = 0.03;
    public double hitRatePerLck = 0.005;

    public double hitRate(EntityLivingBase entity, CombatantInfo info) {
        final double overrideRate = Combat.getWeaponInfo(entity, EnumHand.MAIN_HAND)
                .map(wo -> wo.hitRate).orElse(0d);

        final double perFactor = info.is(WeaponTrait.melee) ? hitRateMeleePerPer : hitRateRangePerPer;
        final double perception = CharAttribute.per.get(info);
        final double luck = CharAttribute.lck.get(info);
        return hitRateBase + perception * perFactor + luck * hitRatePerLck + overrideRate;
    }

    public double evasionPerRef = 0.03;
    public double evasionPerLck = 0.005;
    public double rangedEvasionPenalty = 0.2;
    public double noShieldEvasionPenalty = 0.25;

    public double evasion(EntityLivingBase victim, CombatantInfo vicInfo, CombatantInfo srcInfo) {
        final double reflexes = CharAttribute.ref.get(vicInfo);
        final double luck = CharAttribute.lck.get(vicInfo);
        final double penalty = evasionPenalty(vicInfo, srcInfo);
        final double factor = evasionFactor(victim, vicInfo, srcInfo);
        return (reflexes * evasionPerRef + luck * evasionPerLck - penalty) * factor;
    }

    public double evasionPenalty(CombatantInfo vicInfo, CombatantInfo srcInfo) {
        double penalty = 0;

        if (srcInfo.is(WeaponTrait.melee)) {
            if (vicInfo.is(WeaponTrait.melee)) {
                if (!vicInfo.isAny(WeaponTrait.evadeMelee)) penalty += noShieldEvasionPenalty;
            } else {
                if (!vicInfo.isAny(WeaponTrait.evadeMelee)) penalty += rangedEvasionPenalty;
            }
        }

        return penalty;
    }

    public double evasionFactor(EntityLivingBase victim, CombatantInfo vicInfo, CombatantInfo srcInfo) {
        if (!srcInfo.is(WeaponTrait.range)) return 1;
        if (vicInfo.isAny(WeaponTrait.evadeRange)) return 1;
        return coverBlocks(victim.world, vicInfo.pos, srcInfo.pos);
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

    public double attackApBase = 6;
    public double attackApDefault = 4;

    public double attackActionPoints(EntityLivingBase entity, Item item) {
        final double overrideAp = Combat.getWeaponInfo(item)
                .map(wo -> wo.apHit).orElse(0d);

        final IAttributeInstance attackSpeed = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        if (attackSpeed != null) { // Can be null for mobs
            return attackApBase - attackSpeed.getAttributeValue() + overrideAp;
        } else {
            return attackApDefault + overrideAp;
        }
    }

    public double movementApFactor = 1;
    public double movementApRiseFactor = 1;
    public double movementApDescentFactor = 0;

    public double movementActionPoints(Vec3d from, Vec3d to) {
        final double distance = from.distanceTo(to) * movementApFactor;
        final double verticalDelta = to.y - from.y;
        if (verticalDelta > 0) {
            return distance + verticalDelta * movementApRiseFactor;
        } else if (verticalDelta < 0) {
            return distance - verticalDelta * movementApDescentFactor;
        } else {
            return distance;
        }
    }

    public double usageApBase = 5;

    public double usageActionPoints(Item item) {
        final double overrideAp = Combat.getWeaponInfo(item)
                .map(wo -> wo.apUse).orElse(0d);
        return usageApBase + overrideAp;
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
