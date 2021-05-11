package msifeed.misca;

import msifeed.misca.combat.ItemCombatTool;
import msifeed.misca.content.*;
import msifeed.misca.supplies.ItemSuppliesBeacon;
import msifeed.misca.supplies.ItemSuppliesInvoice;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.stream.Stream;

public enum MiscaThings {
    INSTANCE;

    public static final ItemSuppliesInvoice suppliesInvoice = new ItemSuppliesInvoice();
    public static final ItemSuppliesBeacon suppliesBeacon = new ItemSuppliesBeacon();
    public static final Item combatTool = new ItemCombatTool();


    public static final Block standingNote = new BlockStandingNote();
    public static final Block wallNote = new BlockWallNote();
    public static final Item itemNote = new ItemNotification();

    public static void init() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);

        GameRegistry.registerTileEntity(TileNotification.class, TileNotification.ID);

        if (FMLCommonHandler.instance().getSide().isClient()) {
            registerRenders();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerRenders() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileNotification.class, new TileNotificationRenderer());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                suppliesInvoice,
                suppliesBeacon,
                combatTool,
                itemNote
        );
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                standingNote,
                wallNote
        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        Stream.of(
                suppliesInvoice,
                suppliesBeacon,
                combatTool,
                itemNote
        ).forEach(MiscaThings::registerItemModel);
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModel(@Nonnull Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
    }
}
