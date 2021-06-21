package msifeed.misca.potions;

import cubex2.cs4.api.WrappedPotionEffect;
import cubex2.cs4.plugins.vanilla.ContentItemFood;
import cubex2.cs4.plugins.vanilla.item.ItemWithSubtypes;
import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharNeed;
import msifeed.misca.charstate.CharstateConfig;
import msifeed.misca.charstate.cap.CharstateProvider;
import msifeed.misca.charstate.cap.CharstateSync;
import msifeed.misca.charstate.cap.ICharstate;
import msifeed.misca.charstate.handler.CorruptionHandler;
import msifeed.misca.charstate.handler.SanityHandler;
import msifeed.sys.cap.FloatContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class DrugPotion extends Potion {
    public final CharNeed need;
    public final double modifier;
    private final IAttribute attribute;

    public DrugPotion(CharNeed need, IAttribute attribute, double modifier, int liquidColorIn) {
        super(modifier < 0, liquidColorIn);
        this.need = need;
        this.modifier = modifier;
        this.attribute = attribute;
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

        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final ICharstate state = CharstateProvider.get(player);

        final double sanityFactor;
        if (need == CharNeed.INT || need == CharNeed.STA)
            sanityFactor = SanityHandler.getRestoreDebuffMod(player);
        else
            sanityFactor = 0;

        final double factor = 1 - state.tolerances().get(need) + sanityFactor;

        final double consumed = modifier * (amplifier + 1);
        final double restored = consumed * factor;
        inst.setBaseValue(attribute.clampValue(inst.getBaseValue() + restored));

        final double toleranceGain = consumed * config.toleranceGainPerPoint;
        state.tolerances().inc(need, (float) toleranceGain);

        if (!player.world.isRemote) {
            CharstateSync.sync((EntityPlayerMP) player);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void addToleranceToTooltip(ItemTooltipEvent event) {
        final ItemStack stack = event.getItemStack();
        final Item item = event.getItemStack().getItem();
        if (!(item instanceof ItemFood && item instanceof ItemWithSubtypes)) return;

        final ContentItemFood content = (ContentItemFood) ((ItemWithSubtypes) stack.getItem()).getContent();
        final WrappedPotionEffect[] effects = content.potionEffects.get(stack.getItemDamage()).orElse(null);
        if (effects == null) return;

        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return; // Happens during startup

        final FloatContainer<CharNeed> tolerances = CharstateProvider.get(player).tolerances();

        for (WrappedPotionEffect wrap : effects) {
            final PotionEffect effect = wrap.getPotionEffect();
            if (effect == null || !(effect.getPotion() instanceof DrugPotion)) continue;

            final DrugPotion potion = (DrugPotion) effect.getPotion();
            final CharNeed need = potion.need;
            final float tolerance = tolerances.get(need);
            final int toleranceCent = (int) (tolerance * 100);

            event.getToolTip().add(String.format("Толерантность к препаратам %s: %d%%", need.tr(), toleranceCent));
        }
    }
}
