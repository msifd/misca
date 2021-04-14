package msifeed.misca.charsheet;

import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.mixins.PotionMixin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import java.util.Map;
import java.util.Set;

public class BlessingsHandler {
    public static boolean shouldPerformPotionsEffect(EntityLivingBase self) {
        final ICombatant com = CombatantProvider.getOptional(self);
        if (com == null || !com.isInBattle())
            return true;

        final Battle battle = self.world.isRemote
                ? BattleStateClient.STATE
                : Combat.MANAGER.getBattle(com.getBattleId());

        return battle == null || battle.shouldUpdatePotions(self);
    }

    public static void performPotionEffects(EntityPlayer self) {
        final ICharsheet sheet = CharsheetProvider.get(self);
        final Set<Potion> realPotions = self.getActivePotionMap().keySet();

        for (Map.Entry<Potion, Integer> e : sheet.potions().entrySet()) {
            final Potion potion = e.getKey();
            final int amplifier = e.getValue();

            if (realPotions.contains(potion))
                continue; // Real potions will be updated anyway
            if (potion.isReady(self.ticksExisted, amplifier)) // Use entity ticks instead of duration
                potion.performEffect(self, amplifier);
        }
    }

    public static void checkPotionAttributes(EntityPlayer self) {
        final ICharsheet sheet = CharsheetProvider.get(self);
        final AbstractAttributeMap attrs = self.getAttributeMap();

        for (Map.Entry<Potion, Integer> e : sheet.potions().entrySet()) {
            final Potion potion = e.getKey();
            final int amplifier = e.getValue();

            if (isBlessMissing(attrs, potion))
                potion.applyAttributesModifiersToEntity(self, attrs, amplifier);
        }
    }

    private static boolean isBlessMissing(AbstractAttributeMap attrs, Potion potion) {
        for (Map.Entry<IAttribute, AttributeModifier> e : ((PotionMixin) potion).getAttributes().entrySet()) {
            final IAttributeInstance instance = attrs.getAttributeInstance(e.getKey());
            if (instance == null) continue;
            if (!instance.hasModifier(e.getValue())) return true;
        }

        return false;
    }
}
