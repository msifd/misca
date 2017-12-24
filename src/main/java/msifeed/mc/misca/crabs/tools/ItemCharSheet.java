package msifeed.mc.misca.crabs.tools;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.misca.crabs.client.hud.CharacterHud;
import msifeed.mc.misca.crabs.client.hud.HudManager;
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
    @SideOnly(Side.CLIENT)
    public void onEntityInteract(EntityInteractEvent event) {
        // ПКМ листком по существу для редактирования его стат
        if (!(event.target instanceof EntityLivingBase)) return;

        final ItemStack itemStack = event.entityPlayer.getHeldItem();
        if (itemStack == null || !(itemStack.getItem() instanceof ItemCharSheet)) return;

        CharacterHud.INSTANCE.setEntity((EntityLivingBase) event.target);
        HudManager.INSTANCE.openHud(CharacterHud.INSTANCE);
    }
}
