package msifeed.misca.charsheet;

import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.mixins.PotionMixin;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import java.util.Map;
import java.util.Set;

public class BlessingsFlow {
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
