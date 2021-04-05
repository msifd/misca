package msifeed.misca.potions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;

import javax.annotation.Nullable;

public class InstantPotion extends Potion {
    private final IAttribute attribute;
    private final double modifier;

    public InstantPotion(IAttribute attribute, double modifier, int liquidColorIn) {
        super(modifier < 0, liquidColorIn);
        this.attribute = attribute;
        this.modifier = modifier;
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase entity, AbstractAttributeMap attributes, int amplifier) {
        final IAttributeInstance inst = attributes.getAttributeInstance(attribute);
        if (inst == null) return;

        final double value = modifier * (amplifier + 1);
        inst.setBaseValue(attribute.clampValue(inst.getBaseValue() + value));
    }

    @Override
    public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entity, int amplifier, double health) {
        final IAttributeInstance inst = entity.getEntityAttribute(attribute);
        if (inst == null) return;

        final double value = modifier * (amplifier + 1);
        inst.setBaseValue(attribute.clampValue(inst.getBaseValue() + value));
    }
}
