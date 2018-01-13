package msifeed.mc.misca.utils;

import cpw.mods.fml.common.network.IGuiHandler;
import msifeed.mc.misca.books.GuiEditBook;
import msifeed.mc.misca.books.GuiReadBook;
import msifeed.mc.misca.books.ItemRemoteBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MiscaGuiHandler implements IGuiHandler {
    public static final int GUI_REMOTE_BOOK = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUI_REMOTE_BOOK:
                final ItemStack itemStack = player.getHeldItem();
                if (itemStack.getItem() instanceof ItemRemoteBook) {
                    if (itemStack.hasTagCompound())
                        return new GuiReadBook(itemStack);
                    else
                        return new GuiEditBook();
                }
                break;
        }

        return null;
    }
}
