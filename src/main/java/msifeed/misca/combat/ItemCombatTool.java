package msifeed.misca.combat;

import msifeed.misca.Misca;
import msifeed.misca.MiscaPerms;
import msifeed.misca.combat.client.GuiScreenCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCombatTool extends Item {
    public static final String ID = "combat_tool";

    public ItemCombatTool() {
        setRegistryName(Misca.MODID, ID);
        setUnlocalizedName(ID);
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (MiscaPerms.isGameMaster(playerIn)) return false;
        if (FMLCommonHandler.instance().getSide().isClient())
            Minecraft.getMinecraft().displayGuiScreen(new GuiScreenCombat(target));
        return true;
    }
}
