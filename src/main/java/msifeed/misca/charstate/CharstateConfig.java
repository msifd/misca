package msifeed.misca.charstate;

public class CharstateConfig {
    public double globalMiningSpeedModifier = 0.5;

    public double integrityRestPerSec = 0.000016;
    public double integrityCostPerDamage = 0.1;

    public double sanityRestPerFood = 1;
    public double sanityRestPerSpeechChar = 0.001;
    public double sanityRestModPerPsySkill = 0.15;
    public double sanityCostPerSec = 0.000027;
    public double sanityCostPerSecInDarkness = 0.000055;
    public double sanityCostPerDamage = 0.1;

    public double staminaRestPerSec = 0.0014;
    public long staminaRestMiningTimeoutSec = 2;
    public double staminaMiningSlowdownThreshold = 0.25;
    public double staminaCostPerMiningTick = 0.01;
    public double staminaCostPerIngredient = 0.5;
    public int craftMaxIngredientsOfOneType = 4;

    public double corruptionLostPerSec = 0.00000115;

    public float effortRestPerSec = 0.00027f;

    public float survivalSkillNeedsLostFactor = -0.15f;
    public float survivalSkillCraftCostFactor = -0.05f;
    public float survivalSkillEffortsRestFactor = 0.15f;
    public float workSkillMiningSpeedFactor = 0.33f;
    public float workSkillCraftCostFactor = -0.02f;

    public int ordIncreaseAmount = 2;
    public long ordIncreaseIntervalSec = 24 * 60 * 60;
    public long ordIncreaseMaxWindowSec = 7 * 24 * 60 * 60;
    public int ordToSealRate = 40;
}
