package msifeed.mc.misca.crabs.tools;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.crabs.client.CharacterHud;
import msifeed.mc.misca.crabs.client.HudManager;
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

public class ItemCharSheet extends Item {
    public ItemCharSheet() {
        setUnlocalizedName("char_sheet");
        setTextureName("paper");
        setCreativeTab(MiscaThings.tab);
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
        lines.add(MiscaUtils.l10n("item.char_sheet.desc"));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
        return false;
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        // Обработка только на сервере
        // ПКМ листком по существу для редактирования его стат
        if (event.entityPlayer.worldObj.isRemote || !(event.target instanceof EntityLivingBase)) return;

        final ItemStack itemStack = event.entityPlayer.getHeldItem();
        if (itemStack == null || !(itemStack.getItem() instanceof ItemCharSheet)) return;

        CharacterHud.INSTANCE.setEntity((EntityLivingBase) event.target);
        HudManager.INSTANCE.openHud(CharacterHud.INSTANCE);
    }
}
