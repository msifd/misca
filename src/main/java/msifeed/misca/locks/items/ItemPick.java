package msifeed.misca.locks.items;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharSkill;
import msifeed.misca.locks.LockType;
import msifeed.misca.locks.LockUtils;
import msifeed.misca.locks.LocksConfig;
import msifeed.misca.locks.cap.ILockHolder;
import msifeed.misca.locks.cap.LockAccessor;
import msifeed.misca.locks.cap.pick.ILockPick;
import msifeed.misca.locks.cap.pick.LockPickProvider;
import msifeed.misca.rolls.Dices;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.Random;

public class ItemPick extends Item implements IUnlockTool {
    private static final String ID_BASE = "pick_";
    private static final int MAX_DURATION = 72000;
    private static final int LOCKING_DURATION = 32;

    private final LockType type;

    public ItemPick(LockType type) {
        this.type = type;

        setRegistryName(Misca.MODID, ID_BASE + type.name());
        setTranslationKey(ID_BASE + type.name());
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new LockPickProvider();
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!player.isSneaking()) return EnumActionResult.FAIL;
        if (hand != EnumHand.MAIN_HAND) return EnumActionResult.FAIL;

        final int thievery = CharSkill.thievery.get(player);
        if (thievery < 1) return EnumActionResult.FAIL;

        final ILockHolder lock = LockAccessor.createWrap(world, pos);
        if (lock == null || !lock.hasSecret() || lock.getType() != type) return EnumActionResult.FAIL;

        final ILockPick pick = LockPickProvider.get(player.getHeldItemMainhand());
        if (pick == null) return EnumActionResult.FAIL;

        pick.setLock(pos, lock.isLocked() ? lock.getSecret() : 0);

        player.setActiveHand(hand);

        return EnumActionResult.PASS;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (!entity.isSneaking()) {
            entity.stopActiveHand();
            return;
        }

        if (entity.world.isRemote || !(entity instanceof EntityPlayer)) return;

        final EntityPlayer player = (EntityPlayer) entity;
        final int passed = MAX_DURATION - count;
        final LocksConfig config = Misca.getSharedConfig().locks;

        final int ticksPerPin = LOCKING_DURATION + 1;
        if (passed % ticksPerPin != LOCKING_DURATION) return;

        final ILockPick pick = LockPickProvider.get(stack);
        if (pick == null) return;

        final World world = player.world;
        if (pick.isLocked()) {
            final int pinsTried = passed / ticksPerPin;
            final int pin = LockUtils.getFirstFreePin(pick.getSecret(), pinsTried);
            final int pos = LockUtils.getPinPos(pick.getSecret(), pin);

            final int skill = CharSkill.thievery.get(player);
            final int maxPin = 2 + skill * 2;
            if (pin > maxPin) return;

            if (pin >= 0) {
                stack.setItemDamage(0);

                final double chance = config.pinPickChanceBase - pos * config.pinPickChancePosMod;
                if (world.rand.nextFloat() < chance) {
                    pick.setPin(pin);
                    if (!player.world.isRemote)
                        player.sendStatusMessage(new TextComponentString(getSetMessage(world.rand, pin + 1)), false);
                } else {
                    if (!player.world.isRemote)
                        player.sendStatusMessage(new TextComponentString(getFailSetMessage(world.rand, pin + 1)), false);

                    if (Dices.check(config.pickBreakChance)) {
                        player.renderBrokenItemStack(stack);
                        stack.shrink(1);
                        player.stopActiveHand();
                        return;
                    }
                }
            }

            if (pick.isLocked()) return;
        }

        final ILockHolder lock = LockAccessor.createWrap(world, pick.getPos());
        if (lock == null || !lock.hasSecret()) return;

        lock.setLocked(!lock.isLocked());

        if (!world.isRemote) {
            final ITextComponent tc = new TextComponentString(lock.isLocked() ? "Locked" : "Unlocked");
            tc.getStyle().setColor(TextFormatting.GREEN);
            player.sendStatusMessage(tc, false);
        }

        player.stopActiveHand();
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return MAX_DURATION;
    }

    private static String getSetMessage(Random rand, int pin) {
        switch (rand.nextInt(6)) {
            default: return "A click out of " + pin;
            case 1: return "Nice click out of " + pin;
            case 2: return pin + " is set";
            case 3: return pin + " is binging... nice click there";
            case 4: return pin + " is fine";
        }
    }

    private static String getFailSetMessage(Random rand, int pin) {
        if (rand.nextInt(4) == 0)
            return pin + " is binding";
        return "Nothing on " + pin;
    }
}
