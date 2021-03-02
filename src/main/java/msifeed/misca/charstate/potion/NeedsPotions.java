package msifeed.misca.charstate.potion;

import msifeed.misca.Misca;
import msifeed.misca.charstate.handler.CorruptionHandler;
import msifeed.misca.charstate.handler.IntegrityHandler;
import msifeed.misca.charstate.handler.SanityHandler;
import msifeed.misca.charstate.handler.StaminaHandler;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NeedsPotions {
    public static final Potion RESTORE_INT = new InstantNeedsPotion(IntegrityHandler.INTEGRITY, 5, 3151804)
            .setPotionName("effect.restoreIntegrity").setRegistryName(Misca.MODID, "restoreIntegrity");
    public static final Potion DAMAGE_INT = new InstantNeedsPotion(IntegrityHandler.INTEGRITY, -5, 1151804)
            .setPotionName("effect.damageIntegrity").setRegistryName(Misca.MODID, "damageIntegrity");
    public static final Potion BUFF_INT = new TemporaryNeedsPotion(false, 3111504)
            .setPotionName("effect.buffIntegrity").setRegistryName(Misca.MODID, "buffIntegrity")
            .registerPotionAttributeModifier(IntegrityHandler.INTEGRITY, "7ed0b78e-7367-4276-b45c-665e388c9053", 0.01, 2);
    public static final Potion DEBUFF_INT = new TemporaryNeedsPotion(true, 1111504)
            .setPotionName("effect.debuffIntegrity").setRegistryName(Misca.MODID, "debuffIntegrity")
            .registerPotionAttributeModifier(IntegrityHandler.INTEGRITY, "0158c929-b3c0-485e-9174-55aeb35a0333", -0.01, 2);

    public static final Potion RESTORE_SAN = new InstantNeedsPotion(SanityHandler.SANITY, 5, 3151804)
            .setPotionName("effect.restoreSanity").setRegistryName(Misca.MODID, "restoreSanity");
    public static final Potion DAMAGE_SAN = new InstantNeedsPotion(SanityHandler.SANITY, -5, 1151804)
            .setPotionName("effect.damageSanity").setRegistryName(Misca.MODID, "damageSanity");
    public static final Potion BUFF_SAN = new TemporaryNeedsPotion(false, 3111504)
            .setPotionName("effect.buffSanity").setRegistryName(Misca.MODID, "buffSanity")
            .registerPotionAttributeModifier(SanityHandler.SANITY, "5f87931f-d54e-4d32-8c7d-099c7fce0be3", 0.01, 2);
    public static final Potion DEBUFF_SAN = new TemporaryNeedsPotion(true, 1111504)
            .setPotionName("effect.debuffSanity").setRegistryName(Misca.MODID, "debuffSanity")
            .registerPotionAttributeModifier(SanityHandler.SANITY, "83ee56ba-e229-49b1-989f-af59f8ad85b5", -0.01, 2);

    public static final Potion RESTORE_STA = new InstantNeedsPotion(StaminaHandler.STAMINA, 5, 3151804)
            .setPotionName("effect.restoreStamina").setRegistryName(Misca.MODID, "restoreStamina");
    public static final Potion DAMAGE_STA = new InstantNeedsPotion(StaminaHandler.STAMINA, -5, 1151804)
            .setPotionName("effect.damageStamina").setRegistryName(Misca.MODID, "damageStamina");

    public static final Potion RESTORE_COR = new InstantNeedsPotion(CorruptionHandler.CORRUPTION, 5, 3151804)
            .setPotionName("effect.restoreCorruption").setRegistryName(Misca.MODID, "restoreCorruption");
    public static final Potion DAMAGE_COR = new InstantNeedsPotion(CorruptionHandler.CORRUPTION, -5, 1151804)
            .setPotionName("effect.damageCorruption").setRegistryName(Misca.MODID, "damageCorruption");
    public static final Potion BUFF_COR = new TemporaryNeedsPotion(false, 3111504)
            .setPotionName("effect.buffCorruption").setRegistryName(Misca.MODID, "buffCorruption")
            .registerPotionAttributeModifier(CorruptionHandler.CORRUPTION, "621133ab-8d2d-436d-9846-4c77e2d47c51", 0.01, 2);
    public static final Potion DEBUFF_COR = new TemporaryNeedsPotion(true, 1111504)
            .setPotionName("effect.debuffCorruption").setRegistryName(Misca.MODID, "debuffCorruption")
            .registerPotionAttributeModifier(CorruptionHandler.CORRUPTION, "b52d5b3b-6995-4b95-91df-4fbd6720e2be", -0.01, 2);

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(RESTORE_INT);
        event.getRegistry().register(DAMAGE_INT);
        event.getRegistry().register(BUFF_INT);
        event.getRegistry().register(DEBUFF_INT);
        event.getRegistry().register(RESTORE_SAN);
        event.getRegistry().register(DAMAGE_SAN);
        event.getRegistry().register(BUFF_SAN);
        event.getRegistry().register(DEBUFF_SAN);
        event.getRegistry().register(RESTORE_STA);
        event.getRegistry().register(DAMAGE_STA);
        event.getRegistry().register(RESTORE_COR);
        event.getRegistry().register(DAMAGE_COR);
        event.getRegistry().register(BUFF_COR);
        event.getRegistry().register(DEBUFF_COR);
    }
}