package msifeed.misca.potions;

import msifeed.misca.Misca;
import msifeed.misca.charstate.handler.CorruptionHandler;
import msifeed.misca.charstate.handler.IntegrityHandler;
import msifeed.misca.charstate.handler.SanityHandler;
import msifeed.misca.charstate.handler.StaminaHandler;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NeedsPotions {
    public static final Potion RESTORE_INT = instant(IntegrityHandler.INTEGRITY, "restoreIntegrity", 5);
    public static final Potion DAMAGE_INT = instant(IntegrityHandler.INTEGRITY, "damageIntegrity", -5);
    public static final Potion BUFF_INT = temp(IntegrityHandler.INTEGRITY, "buffIntegrity", "7ed0b78e-7367-4276-b45c-665e388c9053", 0.01, 2);
    public static final Potion DEBUFF_INT = temp(IntegrityHandler.INTEGRITY, "debuffIntegrity", "0158c929-b3c0-485e-9174-55aeb35a0333", -0.01, 2);

    public static final Potion RESTORE_SAN = instant(SanityHandler.SANITY, "restoreSanity", 5);
    public static final Potion DAMAGE_SAN = instant(SanityHandler.SANITY, "damageSanity", -5);
    public static final Potion BUFF_SAN = temp(SanityHandler.SANITY, "buffSanity", "5f87931f-d54e-4d32-8c7d-099c7fce0be3", 0.01, 2);
    public static final Potion DEBUFF_SAN = temp(SanityHandler.SANITY, "debuffSanity", "83ee56ba-e229-49b1-989f-af59f8ad85b5", -0.01, 2);

    public static final Potion RESTORE_STA = instant(StaminaHandler.STAMINA, "restoreStamina", 5);
    public static final Potion DAMAGE_STA = instant(StaminaHandler.STAMINA, "damageStamina", -5);

    public static final Potion RESTORE_COR = instant(CorruptionHandler.CORRUPTION, "restoreCorruption", 5);
    public static final Potion DAMAGE_COR = instant(CorruptionHandler.CORRUPTION, "damageCorruption", -5);
    public static final Potion BUFF_COR = temp(CorruptionHandler.CORRUPTION, "buffCorruption", "621133ab-8d2d-436d-9846-4c77e2d47c51", 0.01, 2);
    public static final Potion DEBUFF_COR = temp(CorruptionHandler.CORRUPTION, "debuffCorruption", "b52d5b3b-6995-4b95-91df-4fbd6720e2be", -0.01, 2);

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

    private static Potion instant(IAttribute attr, String name, double modifier) {
        return new InstantPotion(attr, modifier, 0x707070)
                .setPotionName("effect." + name).setRegistryName(Misca.MODID, name);
    }

    private static Potion temp(IAttribute attr, String name, String uuid, double amount, int op) {
        return new TemporaryPotion(amount < 0, 0x707070)
                .setPotionName("effect." + name).setRegistryName(Misca.MODID, name)
                .registerPotionAttributeModifier(attr, uuid, amount, op);
    }
}