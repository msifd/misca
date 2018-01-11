package msifeed.mc.misca.books;

import msifeed.mc.misca.Misca;
import msifeed.mc.misca.utils.MiscaGuiHandler;
import msifeed.mc.misca.things.MiscaThings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemRemoteBook extends Item {
    public ItemRemoteBook() {
        setUnlocalizedName("remote_book");
        setTextureName("book_normal");
        setCreativeTab(MiscaThings.itemsTab);
        setMaxStackSize(1);
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    @Override
    public boolean getShareTag() {
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        final ChunkCoordinates c = player.getPlayerCoordinates();
        player.openGui(Misca.INSTANCE, MiscaGuiHandler.GUI_REMOTE_BOOK, world, c.posX, c.posY, c.posZ);

        return itemStack;
    }
}
