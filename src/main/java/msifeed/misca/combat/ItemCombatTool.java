package msifeed.misca.combat;

import msifeed.misca.Misca;
import msifeed.misca.client.ScreenCombat;
import msifeed.misca.client.ScreenCombatAttributes;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemCombatTool extends Item {
    public static final String ID = "combat_tool";

    public ItemCombatTool() {
        setRegistryName(Misca.MODID, ID);
        setTranslationKey(ID);
        setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        if (!entity.isSneaking())
            Minecraft.getMinecraft().displayGuiScreen(new ScreenCombat());
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            final ICombatant com = CombatantProvider.get(player);
            if (com.hasPuppet()) {
                if (player.world.isRemote) {
                    com.resetPuppet();
                    CombatantSync.sync(player);
                    player.sendStatusMessage(new TextComponentString("Reset puppet"), true);
                }
            } else {
                return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
            }
        } else {
            if (player.world.isRemote) {
                Minecraft.getMinecraft().displayGuiScreen(new ScreenCombatAttributes(player));
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (player.isSneaking()) {
            final ICombatant com = CombatantProvider.get(player);
            com.setPuppet(target.getEntityId());
            if (player.world.isRemote) {
                CombatantSync.sync(player);
                player.sendStatusMessage(new TextComponentString("Set puppet: " + target.getName()), true);
            }
        } else {
            if (player.world.isRemote) {
                Minecraft.getMinecraft().displayGuiScreen(new ScreenCombatAttributes(target));
            }
        }

        return true;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        return false;
    }
}
