package msifeed.misca.books;

import msifeed.misca.Misca;
import msifeed.misca.MiscaThings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.net.URI;
import java.util.List;

public class ItemExternalBook extends Item {
    public static final String ID = "external_book";
    public static final String TAG_INDEX = "BookIndex";

    public static ItemStack createStack(String index) {
        final ItemStack stack = new ItemStack(MiscaThings.externalBook);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString(TAG_INDEX, index);
        return stack;
    }

    public ItemExternalBook() {
        setRegistryName(Misca.MODID, ID);
        setTranslationKey(ID);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

        final ItemStack stack = player.getHeldItem(hand);
        final NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey(TAG_INDEX, 8)) // 8 - String
            return new ActionResult<>(EnumActionResult.FAIL, stack);

        if (world.isRemote) {
            final String index = nbt.getString(TAG_INDEX);
            final String url = String.format(Misca.getSharedConfig().externalBookUrlTemplate, index);
            openUrlScreen(url);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    private static void openUrlScreen(String url) {
        final Minecraft mc = Minecraft.getMinecraft();

        mc.displayGuiScreen(new GuiConfirmOpenLink((result, id) -> {
            if (result) {
                openUrlInBrowser(url);
            }
            mc.displayGuiScreen(null);
        }, url, 31102009, true));
    }

    @SideOnly(Side.CLIENT)
    private static void openUrlInBrowser(String url) {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
            return;

        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
        if (flag.isAdvanced()) {
            final NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null || !nbt.hasKey(TAG_INDEX, 8)) // 8 - String
                return;
            final String index = nbt.getString(TAG_INDEX);
            tooltip.add("Index: " + index);
        }
    }
}
