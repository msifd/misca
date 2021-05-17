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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCombatTool extends Item {
    public static final String ID = "combat_tool";

    public ItemCombatTool() {
        setRegistryName(Misca.MODID, ID);
        setTranslationKey(ID);
        setCreativeTab(CreativeTabs.COMBAT);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        if (!entity.isSneaking() && FMLCommonHandler.instance().getSide().isClient()) {
            openCombatGui();
        }
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            final ICombatant com = CombatantProvider.get(player);
            if (com.hasPuppet()) {
                if (!player.world.isRemote) {
                    com.resetPuppet();
                    CombatantSync.sync(player);
                    player.sendStatusMessage(new TextComponentString("Reset puppet"), true);
                }
            } else {
                return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
            }
        } else {
            if (player.world.isRemote && FMLCommonHandler.instance().getSide().isClient()) {
                openAttributesGui(player);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    /**
     * Move here to handle CustomNPC interactions
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInteractWithEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!(event.getTarget() instanceof EntityLivingBase)) return;
        final EntityLivingBase target = (EntityLivingBase) event.getTarget();

        final EntityPlayer player = event.getEntityPlayer();
        if (player.getHeldItem(event.getHand()).getItem() != this) return;

        if (player.isSneaking()) {
            final ICombatant com = CombatantProvider.get(player);
            com.setPuppet(target.getEntityId());
            if (!player.world.isRemote) {
                CombatantSync.sync(player);
                player.sendStatusMessage(new TextComponentString("Set puppet: " + target.getName()), true);
            }
        } else {
            if (player.world.isRemote && FMLCommonHandler.instance().getSide().isClient()) {
                openAttributesGui(target);
            }
        }
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    private void openCombatGui() {
        Minecraft.getMinecraft().displayGuiScreen(new ScreenCombat());
    }

    @SideOnly(Side.CLIENT)
    private void openAttributesGui(EntityLivingBase target) {
        Minecraft.getMinecraft().displayGuiScreen(new ScreenCombatAttributes(target));
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
