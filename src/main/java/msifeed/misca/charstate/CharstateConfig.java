package msifeed.misca.charstate;

public class CharstateConfig {
    public double globalMiningSpeedModifier = 0.5;

    public double integrityRestPerSec = 0.000016;
    public double integrityCostPerDamage = 0.1;

    public double sanityRestPerFood = 1;
    public double sanityRestPerSpeechChar = 0.001;
    public double sanityCostPerSec = 0.000027;
    public double sanityCostPerSecInDarkness = 0.000055;
    public double sanityCostPerDamage = 0.1;

    public double staminaRestPerSec = 0.000014;
    public double staminaRestTimeoutSec = 2;
    public double staminaCostPerMiningTick = 0.0001;
    public double staminaCostPerSupplyItem = 0.005;

    public double corruptionLostPerSec = 0.00000115;

    public float effortRestPerSec = 0.00027f;
}
