package msifeed.misca.combat.rules;

import msifeed.misca.charsheet.CharAttribute;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.combat.Combat;
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
    public float damageIncreaseMeleePerStr = 0.04f;
    public float damageIncreaseRangePerStr = 0.02f;

    public float damageIncrease(CombatantInfo info) {
        final float strFactor = info.is(WeaponTrait.melee) ? damageIncreaseMeleePerStr : damageIncreaseRangePerStr;
        return info.cs.attrs().get(CharAttribute.str) * strFactor;
    }

    public float damageAbsorptionPerEnd = 0.02f;

    public float damageAbsorption(ICharsheet cs) {
        return cs.attrs().get(CharAttribute.end) * damageAbsorptionPerEnd;
    }

    public double hitRateBase = 0.5;
    public double hitRateMeleePerPer = 0.015;
    public double hitRateRangePerPer = 0.03;
    public double hitRatePerLck = 0.005;

    public double hitRate(EntityLivingBase entity, CombatantInfo info) {
        final double overrideRate = Combat.getConfig().getWeaponOverride(entity)
                .map(wo -> wo.hitRate).orElse(0d);

        final double perFactor = info.is(WeaponTrait.melee) ? hitRateMeleePerPer : hitRateRangePerPer;
        final int perception = info.cs.attrs().get(CharAttribute.per);
        final int luck = info.cs.attrs().get(CharAttribute.lck);
        return hitRateBase + perception * perFactor + luck * hitRatePerLck + overrideRate;
    }

    public double evasionPerRef = 0.03;
    public double evasionPerLck = 0.005;
    public double meleeWithRangedEvasionPenalty = 0.02;

    public double evasion(EntityLivingBase victim, CombatantInfo vicInfo, CombatantInfo srcInfo) {
        final int reflexes = vicInfo.cs.attrs().get(CharAttribute.ref);
        final int luck = vicInfo.cs.attrs().get(CharAttribute.lck);
        final double weaponTypePenalty = srcInfo.is(WeaponTrait.melee) && !vicInfo.is(WeaponTrait.melee)
                ? meleeWithRangedEvasionPenalty : 0;
        final double totalFactor = srcInfo.is(WeaponTrait.range) ? coverBlocks(victim.world, vicInfo.pos, srcInfo.pos) : 1;

        return (reflexes * evasionPerRef + luck * evasionPerLck - weaponTypePenalty) * totalFactor;
    }

    public double criticalHitBase = 0.01;
    public double criticalHitPerPer = 0.0025;
    public double criticalHitPerLck = 0.0075;

    public double criticalHit(ICharsheet cs) {
        final int perception = cs.attrs().get(CharAttribute.per);
        final int luck = cs.attrs().get(CharAttribute.lck);
        return criticalHitBase + perception * criticalHitPerPer + luck * criticalHitPerLck;
    }

    public double criticalEvasionBase = 0.01;
    public double criticalEvasionPerEnd = 0.0025;
    public double criticalEvasionPerLck = 0.0075;

    public double criticalEvasion(ICharsheet cs) {
        final int endurance = cs.attrs().get(CharAttribute.end);
        final int luck = cs.attrs().get(CharAttribute.lck);
        return criticalEvasionBase + endurance * criticalEvasionPerEnd + luck * criticalEvasionPerLck;
    }

//    public double magicMeleeAttack = 0.75;
//    public double magicMeleeBackfire = 0.5;
//
//    public double magicBackfire() {
//        return magicMeleeBackfire;
//    }

    public double coverPerBlock = 0.5;

    public double coverBlocks(World world, Vec3d vPos, Vec3d sPos) {
        final Vec3d vec = sPos.subtract(vPos);
        final EnumFacing facing = EnumFacing.getFacingFromVector((float) vec.x, 0, (float) vec.z);

        final BlockPos pos = new BlockPos(vPos).offset(facing);
        final BlockPos posLeft = pos.offset(facing.rotateYCCW());
        final BlockPos posRight = pos.offset(facing.rotateY());
        final double coverage = blockCoverage(world, pos) + blockCoverage(world, pos.up())
                + blockCoverage(world, posLeft) + blockCoverage(world, posLeft.up())
                + blockCoverage(world, posRight) + blockCoverage(world, posRight.up());

        return Math.min(coverage * coverPerBlock, 1);
    }

    private static int blockCoverage(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().isPassable(world, pos) ? 0 : 1;
    }

    // Action points

    public double attackApBase = 6;
    public double attackApDefault = 4;

    public double attackActionPoints(EntityLivingBase entity) {
        final double overrideAp = Combat.getConfig().getWeaponOverride(entity)
                .map(wo -> wo.ap).orElse(0d);

        final IAttributeInstance attackSpeed = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        if (attackSpeed != null) {
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

    public double apPerMoveBase = 5;
    public double apPerMovePerAgi = 1;

    public double actionPointsPerMove(ICharsheet cs) {
        return apPerMoveBase + cs.attrs().get(CharAttribute.agi) * apPerMovePerAgi;
    }

    public double maxApBase = 0;
    public double maxApPerAgi = 4;

    public double maxActionPoints(ICharsheet cs) {
        return maxApBase + cs.attrs().get(CharAttribute.agi) * maxApPerAgi;
    }
}