package msifeed.misca.charstate.handler;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharNeed;
import msifeed.misca.charsheet.CharSkill;
import msifeed.misca.charstate.CharstateConfig;
import msifeed.misca.charstate.cap.CharstateProvider;
import msifeed.misca.charstate.cap.CharstateSync;
import msifeed.misca.charstate.cap.ICharstate;
import msifeed.misca.rolls.Dices;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;

public class StaminaHandler {
    public static final IAttribute STAMINA = new RangedAttribute(null, Misca.MODID + ".stamina", 100, 0, 100).setShouldWatch(true);

    public void handleTime(EntityPlayer player, long secs, double factorMod) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final ICharstate state = CharstateProvider.get(player);
        if (state.passedFromMining() < config.staminaRestMiningTimeoutSec) return;

        final double factor = Math.max(0, 1 + factorMod + config.foodRestMod(player) + SanityHandler.getRestoreDebuffMod(player));
        final double restored = secs * config.staminaRestPerSec * factor * CharNeed.STA.gainFactor(player);

        final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() + restored));

        if (Double.isNaN(inst.getBaseValue()))
            inst.setBaseValue(STAMINA.getDefaultValue());
    }

    public void handleMining(PlayerEvent.BreakSpeed event) {
        final EntityPlayer player = event.getEntityPlayer();
        final ICharstate state = CharstateProvider.get(player);

        final CharstateConfig config = Misca.getSharedConfig().charstate;
        state.resetMiningTime();

        final double lost = config.staminaCostPerMiningTick * CharNeed.STA.lostFactor(player);

        final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() - lost));

        final double staminaPercent = inst.getBaseValue() / 100;
        final double staminaFactor = staminaPercent < config.staminaMiningSlowdownThreshold
                ? staminaPercent / config.staminaMiningSlowdownThreshold
                : 1;

        final int workSkill = CharSkill.hardworking.get(player);
        final double speedFactor = 1 + workSkill * config.workSkillMiningSpeedFactor;
        final double newSpeed = event.getNewSpeed() * config.globalMiningSpeedModifier * staminaFactor * speedFactor;
        event.setNewSpeed((float) newSpeed);
    }

    public void handleCrafting(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
        final EntityPlayer player = event.player;
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final ICharstate state = CharstateProvider.get(player);

        final int researchLevel = CharSkill.research.get(player);
        final double restoreChance = researchLevel * config.researchSkillRestoreIngredientChance;
        for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
            final ItemStack stack = event.craftMatrix.getStackInSlot(i);
            if (stack.getCount() == stack.getMaxStackSize())
                continue;
            final long nonce = state.nonce() + player.getEntityId(); // Mix in player id to make random more personal
            if (Dices.checkWithNonce(nonce, restoreChance))
                stack.grow(1);
            state.incNonce();
        }

        if (!player.world.isRemote) {
            CharstateSync.syncNonce((EntityPlayerMP) player);

            final double freeChance = researchLevel * config.researchSkillFreeCraftChance;
            if (Dices.check(freeChance)) {
                // Free craft!
                return;
            }

            final double lost = getCraftCost(player, getCraftIngredients(event.craftMatrix));
            final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
            inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() - lost));
        }
    }

    public static int getCraftIngredients(IInventory matrix) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final Map<Item, Integer> uniqueIngredients = new HashMap<>();
        for (int i = 0; i < matrix.getSizeInventory(); i++) {
            final ItemStack stack = matrix.getStackInSlot(i);
            if (!stack.isEmpty())
                uniqueIngredients.compute(stack.getItem(), (k, v) -> (v == null ? 1 : v + 1));
        }
        return uniqueIngredients.values().stream()
                .mapToInt(value -> Math.min(value, config.craftMaxIngredientsOfOneType))
                .sum();
    }

    public static int getCraftIngredients(NonNullList<ItemStack> stacks, int start, int end) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final Map<Item, Integer> uniqueIngredients = new HashMap<>();

        for (int i = start; i < end; i++) {
            final ItemStack stack = stacks.get(i);
            if (!stack.isEmpty())
                uniqueIngredients.compute(stack.getItem(), (k, v) -> (v == null ? 1 : v + 1));
        }
        return uniqueIngredients.values().stream()
                .mapToInt(value -> Math.min(value, config.craftMaxIngredientsOfOneType))
                .sum();
    }

    public static double getCraftCost(EntityPlayer player, int ingredients) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;

        final int survivalSkill = CharSkill.survival.get(player);
        final int workSkill = CharSkill.hardworking.get(player);
        final double factor = Math.max(0.1, 1 + survivalSkill * config.survivalSkillCraftCostFactor + workSkill * config.workSkillCraftCostFactor);

        return ingredients * config.staminaCostPerIngredient * factor * CharNeed.STA.lostFactor(player);
    }

    public static boolean canCraft(EntityPlayer player, int ingredients) {
        if (CorruptionHandler.isCraftDisabled(player))
            return false;

        final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
        return inst.getBaseValue() >= getCraftCost(player, ingredients);
    }

    public static double getStaminaForDelivery(EntityPlayer player) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final double factor = 1 + CharSkill.survival.get(player) * -config.survivalSkillNeedsLostFactor; // factor >= 1
        final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
        return inst.getBaseValue() * factor;
    }

    public static void consumeDelivery(EntityPlayer player, double amount) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final double factor = 1 + CharSkill.survival.get(player) * config.survivalSkillNeedsLostFactor; // factor <= 1
        final double lost = amount * factor * CharNeed.STA.lostFactor(player);

        final IAttributeInstance inst = player.getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() - lost));

        final ITextComponent comp = new TextComponentString("You lost " + lost + " stamina.");
        comp.getStyle().setColor(TextFormatting.DARK_GRAY);
        comp.getStyle().setItalic(true);
        player.sendStatusMessage(comp, false);
    }

    public void handleSpeech(EntityPlayer listener, int chars, double speechMod, double regionMod) {
        final CharstateConfig config = Misca.getSharedConfig().charstate;

        if (listener.getEntityAttribute(SanityHandler.SANITY).getAttributeValue() < config.sanityLevelToRegenStamina)
            return;

        final double factor = Math.max(0, 1 + regionMod + speechMod + config.foodRestMod(listener) + SanityHandler.getRestoreDebuffMod(listener));
        final double restored = chars * config.staminaRestPerSpeechChar * factor * CharNeed.STA.gainFactor(listener);

        final IAttributeInstance inst = listener.getEntityAttribute(STAMINA);
        inst.setBaseValue(STAMINA.clampValue(inst.getBaseValue() + restored));
    }
}
