package msifeed.misca.charstate.handler;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharSkill;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charstate.CharstateConfig;
import msifeed.misca.charstate.cap.CharstateProvider;
import msifeed.misca.charstate.cap.ICharstate;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class StaminaHandler {
    public static final IAttribute STAMINA = new RangedAttribute(null, Misca.MODID + ".stamina", 100, 0, 100).setShouldWatch(true);

    public void handleTime(EntityPlayer player, long secs) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final ICharstate state = CharstateProvider.get(player);
        if (state.passedFromMining() < config.staminaRestTimeoutSec) return;

        final double restored = secs * config.staminaRestPerSec;

        final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() + restored));
    }

    public void handleMining(PlayerEvent.BreakSpeed event) {
        final ICharstate state = CharstateProvider.get(event.getEntityPlayer());

        final CharstateConfig config = Misca.getSharedConfig().charstate;
        state.resetMiningTime();

        final IAttributeInstance inst = event.getEntityPlayer().getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() - config.staminaCostPerMiningTick));

        final int workSkill = CharsheetProvider.get(event.getEntityPlayer()).skills().get(CharSkill.hardworking);
        final double factor = 1 + workSkill * config.workSkillMiningSpeedFactor;
        final double staminaFactor = inst.getBaseValue() / 100;
        final double newSpeed = event.getNewSpeed() * config.globalMiningSpeedModifier * staminaFactor * factor;
        event.setNewSpeed((float) newSpeed);
    }

    public void handleCrafting(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;

        final int results = event.crafting.getCount();
        int ingredients = 0;
        for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
            if (!event.craftMatrix.getStackInSlot(i).isEmpty())
                ingredients++;
        }

        final ICharsheet charsheet = CharsheetProvider.get(event.player);
        final int survivalSkill = charsheet.skills().get(CharSkill.survival);
        final int workSkill = charsheet.skills().get(CharSkill.hardworking);
        final double factor = 1 + survivalSkill * config.survivalSkillNeedsLostFactor + workSkill * config.workSkillCraftCostFactor;

        final int threshold = config.staminaCostIngredientThreshold;
        final double corrIngredients = Math.min(ingredients, threshold)
                + Math.max(0, ingredients - threshold) * (results / (double) ingredients);

        final double lost = corrIngredients * config.staminaCostPerIngredient * factor;

        final IAttributeInstance inst = event.player.getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() - lost));

        if (inst.getBaseValue() < 0.01) {
            event.player.closeScreen();
        }
    }

    public static double getStaminaForDelivery(EntityPlayer player) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final double factor = 1 + CharsheetProvider.get(player).skills().get(CharSkill.survival) * -config.survivalSkillNeedsLostFactor; // factor >= 1
        final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
        return inst.getBaseValue() * factor;
    }

    public static void consumeDelivery(EntityPlayer player, double amount) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final double factor = 1 + CharsheetProvider.get(player).skills().get(CharSkill.survival) * config.survivalSkillNeedsLostFactor; // factor <= 1
        final double lost = amount * factor;

        final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() - lost));

        final ITextComponent comp = new TextComponentString("You lost " + lost + " stamina.");
        comp.getStyle().setColor(TextFormatting.DARK_GRAY);
        comp.getStyle().setItalic(true);
        player.sendStatusMessage(comp, false);
    }
}
