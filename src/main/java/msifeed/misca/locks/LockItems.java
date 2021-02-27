package msifeed.misca.locks;

import msifeed.misca.Misca;
import msifeed.misca.locks.items.ItemKey;
import msifeed.misca.locks.items.ItemLock;
import msifeed.misca.locks.items.ItemPick;
import msifeed.misca.locks.items.ItemSkeletalKey;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LockItems {
    public static final Item lockMechanical = new ItemLock(LockType.mechanical);
    public static final Item lockMagical = new ItemLock(LockType.magical);
    public static final Item lockDigital = new ItemLock(LockType.digital);

    public static final Item pickMechanical = new ItemPick(LockType.mechanical);

    public static final Item key = new ItemKey().setRegistryName(Misca.MODID, ItemKey.ID);
    public static final Item skeletalKey = new ItemSkeletalKey();

    @SideOnly(Side.CLIENT)
    private static final ModelResourceLocation MECH_LOC_MODEL = getModel(lockMechanical.getRegistryName());
    @SideOnly(Side.CLIENT)
    private static final ModelResourceLocation MAGI_LOC_MODEL = getModel(lockMagical.getRegistryName());

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                lockMechanical,
                lockMagical,
                pickMechanical,
                key,
                skeletalKey
        );
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomMeshDefinition(lockMechanical, stack -> MECH_LOC_MODEL);
        ModelBakery.registerItemVariants(lockMechanical, MECH_LOC_MODEL);
        ModelLoader.setCustomMeshDefinition(lockMagical, stack -> MAGI_LOC_MODEL);
        ModelBakery.registerItemVariants(lockMagical, MAGI_LOC_MODEL);

        ModelLoader.setCustomModelResourceLocation(pickMechanical, 0, getModel(pickMechanical.getRegistryName()));

        ModelLoader.setCustomModelResourceLocation(key, 0, getModel(new ResourceLocation(Misca.MODID, "blank_key")));
        ModelLoader.setCustomModelResourceLocation(key, 1, getModel(key.getRegistryName()));
        ModelLoader.setCustomModelResourceLocation(skeletalKey, 0, getModel(skeletalKey.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    private static ModelResourceLocation getModel(ResourceLocation location) {
        return new ModelResourceLocation(location, "inventory");
    }
}
