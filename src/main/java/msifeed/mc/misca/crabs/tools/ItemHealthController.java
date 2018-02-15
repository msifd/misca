package msifeed.mc.misca.crabs.tools;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import msifeed.mc.misca.things.MiscaThings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Arrays;
import java.util.List;

public class ItemHealthController extends Item {
    public ItemHealthController() {
        setUnlocalizedName("health_controller");
        setTextureName("nether_star");
        setCreativeTab(MiscaThings.itemsTab);
        setMaxStackSize(1);
    }

    public void changeHealth(EntityLivingBase entity, boolean isPositive) {
        final float value = isPositive ? 1.0F : -1.0F;
        final float currentEntityHealth = entity.getHealth();
        final float result = currentEntityHealth + value;
        if (result > 0 && result <= entity.getMaxHealth())
            entity.setHealth(result);
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advanced) {
        String desc = LanguageRegistry.instance().getStringLocalization("item.health_controller.desc");
        desc = StringEscapeUtils.unescapeJava(desc);
        lines.addAll(Arrays.asList(desc.split("\n")));
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase player, ItemStack stack) {
        // Shift+ЛКМ - убрать 1 хп у себя

        if (player.isSneaking())
            this.changeHealth(player, false);

        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
        // ЛКМ по существу - убрать 1 хп у существа

        if (!player.isSneaking()) {
            if (player.worldObj.isRemote || !(target instanceof EntityLivingBase)) return true;
            final EntityLivingBase entity = (EntityLivingBase) target;
            this.changeHealth(entity, false);
        }

        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Shift+ПКМ - добавить 1 хп себе

        if (player.isSneaking())
            this.changeHealth(player, true);

        return stack;
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        // ПКМ этим предметом по существу - добавить 1 хп существу

        final ItemStack itemStack = event.entityPlayer.getHeldItem();
        if (itemStack == null || !(itemStack.getItem() instanceof ItemHealthController)) return;

        if (!event.entityPlayer.isSneaking()) {
            if (!(event.target instanceof EntityLivingBase)) return;
            final EntityLivingBase entity = (EntityLivingBase) event.target;
            this.changeHealth(entity, true);
        }
    }
}
