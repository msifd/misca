package msifeed.misca;

import msifeed.misca.supplies.ItemSuppliesBeacon;
import msifeed.misca.supplies.ItemSuppliesInvoice;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.stream.Stream;

public enum MiscaThings {
    INSTANCE;

    public static final ItemSuppliesInvoice suppliesInvoice = new ItemSuppliesInvoice();
    public static final ItemSuppliesBeacon suppliesBeacon = new ItemSuppliesBeacon();

    public static void init() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                suppliesInvoice,
                suppliesBeacon
        );
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        Stream.of(
                suppliesInvoice,
                suppliesBeacon
        ).forEach(MiscaThings::registerItemModel);
    }

    public static void registerItemModel(@Nonnull Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
    }
}
