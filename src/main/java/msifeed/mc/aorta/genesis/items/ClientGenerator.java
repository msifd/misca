package msifeed.mc.aorta.genesis.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.aorta.genesis.items.client.ItemCustomRender;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(Side.CLIENT)
class ClientGenerator {
    static void fillTexture(ItemGenesisUnit unit, Item item) {
        if (unit.texture != null)
            item.setTextureName(unit.texture);
        if (unit.renderData != null)
            MinecraftForgeClient.registerItemRenderer(item, new ItemCustomRender());
    }
}
