package msifeed.mc.misca.crabs.tools;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.misca.things.MiscaThings;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import java.util.List;

public class ItemHealthController extends Item {
    public ItemHealthController() {
        setUnlocalizedName("health_controller");
        setTextureName("nether_star");
        setCreativeTab(MiscaThings.itemsTab);
        setMaxStackSize(1);
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
        lines.add(MiscaUtils.l10n("item.health_controller.desc"));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
        // ЛКМ по существу: добавить 1 хп
        if (player.worldObj.isRemote || !(target instanceof EntityLivingBase)) return true;

        final EntityLivingBase entity = (EntityLivingBase) target;
        final Float currentEntityHealth = entity.getHealth();
        if (currentEntityHealth <= entity.getMaxHealth())
            entity.setHealth(entity.getHealth() + 1.0F);

        return true;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEntityInteract(EntityInteractEvent event) {
        // ПКМ по существу: убрать 1 хп
        if (!(event.target instanceof EntityLivingBase)) return;

        final EntityLivingBase entity = (EntityLivingBase) event.target;
        final Float currentEntityHealth = entity.getHealth();
        if (currentEntityHealth > 1.0F)
            entity.setHealth(entity.getHealth() - 1.0F);
    }
}
