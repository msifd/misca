package msifeed.misca.charstate.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import javax.annotation.Nullable;

public class InstantNeedsPotion extends Potion {
    private final IAttribute attribute;
    private final double modifier;

    public InstantNeedsPotion(IAttribute attribute, double modifier, int liquidColorIn) {
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
    public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entity, int amplifier, double health) {
        if (!(entity instanceof EntityPlayer)) return;

        final double value = modifier * (amplifier + 1);
        final IAttributeInstance inst = entity.getEntityAttribute(attribute);
        inst.setBaseValue(attribute.clampValue(inst.getBaseValue() + value));
    }
}
