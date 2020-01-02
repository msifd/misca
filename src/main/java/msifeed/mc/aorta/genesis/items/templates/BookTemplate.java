package msifeed.mc.aorta.genesis.items.templates;

import msifeed.mc.aorta.genesis.items.ItemGenesisUnit;
import msifeed.mc.aorta.genesis.items.data.BookData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class BookTemplate extends ItemTemplate {
    private final BookData data;

    public BookTemplate(ItemGenesisUnit unit, BookData data) {
        super(unit);
        this.data = data;
    }

    public BookData getData() {
        return data;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        // FIXME: Aorta.GUI_HANDLER.toggleBookViewer(player);
        return itemStack;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean debug) {
        super.addInformation(itemStack, player, lines, debug);
        if (debug)
            lines.add("Index: " + data.index);
    }
}
