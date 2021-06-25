package msifeed.misca.charstate;

import net.minecraft.entity.player.EntityPlayer;

public class CharstateConfig {
    public double globalMiningSpeedModifier = 0.5;

    public double integrityRestPerSec = 0.000016;
    public double integrityCostPerDamage = 0.1;

    public double sanityRestPerFood = 1;
    public double sanityRestPerSpeechChar = 0.001;
    public double sanityCostPerSec = 0.000027;
    public double sanityCostPerSecInDarkness = 0.000055;
    public double sanityCostPerDamage = 0.1;
    public double sanityLevelToRegenStamina = 100;
    public double sanityDebuffToRestThreshold = 0.75;

    public double staminaRestPerSec = 0.0014;
    public long staminaRestMiningTimeoutSec = 2;
    public double staminaRestPerSpeechChar = 0.0005;
    public double staminaMiningSlowdownThreshold = 0.25;
    public double staminaCostPerMiningTick = 0.01;
    public double staminaCostPerIngredient = 0.5;
    public int craftMaxIngredientsOfOneType = 4;

    public double corruptionSilenceToGainSec = 3600;
    public double corruptionGainPerSec = 0.00005;
    public double corruptionLostPerSec = 0.00001;
    public double corruptionDisablePotionsThreshold = 25;
    public double corruptionDisableSkillsThreshold = 50;
    public double corruptionDisableCraft = 75;

    public float effortRestPerSec = 0.00027f;

    public float survivalSkillNeedsLostFactor = -0.15f;
    public float survivalSkillCraftCostFactor = -0.05f;
    public float survivalSkillEffortsRestFactor = 0.15f;
    public float workSkillMiningSpeedFactor = 0.33f;
    public float workSkillCraftCostFactor = -0.02f;

    public double psychologySanityGainFactor = 0.15;
    public double psychologyStaminaGainFactor = 0.05;

    public double researchSkillFreeCraftChance = 0.1;
    public double researchSkillRestoreIngredientChance = 0.05;

    public int ordIncreaseAmount = 2;
    public long ordIncreaseIntervalSec = 24 * 60 * 60;
    public long ordIncreaseMaxWindowSec = 7 * 24 * 60 * 60;
    public int ordToSealRate = 40;

    public int foodEffectThreshold = 16;
    public float foodNeedsRestMod = 0.25f;
    public float foodExhaustionMod = 0.25f;

    public float foodRestMod(EntityPlayer player) {
        final float food = player.getFoodStats().getFoodLevel();
        return food > foodEffectThreshold ? foodNeedsRestMod : 0;
    }

    public float exhaustionMod(EntityPlayer player) {
        final float food = player.getFoodStats().getFoodLevel();
        return food > foodEffectThreshold ? foodExhaustionMod : 0;
    }

    public double toleranceGainPerPoint = 0.2;
    public double toleranceLostBasePerSec = 0.00027;
    public double toleranceLostMinPerSec = 0.00005;

    public double getToleranceLost(double tolerance) {
        return Math.max(toleranceLostBasePerSec * (1 - tolerance), toleranceLostMinPerSec);
    }
}
