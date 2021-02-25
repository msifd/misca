package msifeed.misca.locks;

import msifeed.misca.Misca;
import msifeed.misca.locks.items.ItemKey;
import msifeed.misca.locks.items.ItemLock;
import msifeed.misca.locks.items.ItemSkeletalKey;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.stream.Stream;

public class LockItems {
    public static final Item lockMechanical = new ItemLock(LockType.mechanical);
    public static final Item lockMagical = new ItemLock(LockType.magical);
    public static final Item lockDigital = new ItemLock(LockType.digital);

    public static final Item key = new ItemKey().setRegistryName(Misca.MODID, ItemKey.ID);
    public static final Item skeletalKey = new ItemSkeletalKey();

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                lockMechanical,
                lockMagical,
                key,
                skeletalKey
        );
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        Stream.of(
                lockMechanical,
                lockMagical
        ).forEach(item -> {
            ModelLoader.setCustomMeshDefinition(item, stack -> new ModelResourceLocation(item.getRegistryName(), "inventory"));
            ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        });

        ModelLoader.setCustomModelResourceLocation(key, 0, new ModelResourceLocation(Misca.MODID + ":blank_key", "inventory"));
        ModelLoader.setCustomModelResourceLocation(key, 1, new ModelResourceLocation(key.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(skeletalKey, 0, new ModelResourceLocation(skeletalKey.getRegistryName(), "inventory"));
    }
}
