package msifeed.misca.potions;

import msifeed.misca.charsheet.CharNeed;
import msifeed.misca.charstate.handler.CorruptionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import javax.annotation.Nullable;

public class InstantPotion extends Potion {
    public final CharNeed need;
    private final IAttribute attribute;
    private final double modifier;

    public InstantPotion(CharNeed need, IAttribute attribute, double modifier, int liquidColorIn) {
        super(modifier < 0, liquidColorIn);
        this.need = need;
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
        if (!(entity instanceof EntityPlayer)) return;

        final IAttributeInstance inst = attributes.getAttributeInstance(attribute);
        if (inst == null) return;

        affect((EntityPlayer) entity, inst, amplifier);
    }

    @Override
    public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entity, int amplifier, double health) {
        if (!(entity instanceof EntityPlayer)) return;

        final IAttributeInstance inst = entity.getEntityAttribute(attribute);
        if (inst == null) return;

        affect((EntityPlayer) entity, inst, amplifier);
    }

    private void affect(EntityPlayer player, IAttributeInstance inst, int amplifier) {
        if (CorruptionHandler.isPotionsDisabled(player, need))
            return;

        final double value = modifier * (amplifier + 1);
        inst.setBaseValue(attribute.clampValue(inst.getBaseValue() + value));
    }
}
