package msifeed.misca.locks;

import msifeed.misca.Misca;
import msifeed.misca.locks.items.*;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
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
//    public static final Item lockDigital = new ItemLock(LockType.digital);

    public static final Item pickMechanical = new ItemPick(LockType.mechanical);
    public static final Item pickMagical = new ItemPick(LockType.magical);

    public static final Item key = new ItemKey().setRegistryName(Misca.MODID, ItemKey.ID);
    public static final Item skeletalKey = new ItemSkeletalKey();

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                lockMechanical,
                lockMagical,
                pickMechanical,
                pickMagical,
                key,
                skeletalKey
        );
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().registerAll(
                new RecipeKeyCloning(key).setRegistryName(Misca.MODID, "clone_mechanical_key")
        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomMeshDefinition(lockMechanical, stack -> LockModels.MECH_LOC_MODEL);
        ModelBakery.registerItemVariants(lockMechanical, LockModels.MECH_LOC_MODEL);
        ModelLoader.setCustomMeshDefinition(lockMagical, stack -> LockModels.MAGI_LOC_MODEL);
        ModelBakery.registerItemVariants(lockMagical, LockModels.MAGI_LOC_MODEL);

        ModelLoader.setCustomModelResourceLocation(pickMechanical, 0, LockModels.getModel(pickMechanical.getRegistryName()));
        ModelLoader.setCustomModelResourceLocation(pickMagical, 0, LockModels.getModel(pickMagical.getRegistryName()));

        ModelLoader.setCustomModelResourceLocation(key, 0, LockModels.getModel(new ResourceLocation(Misca.MODID, "blank_key")));
        ModelLoader.setCustomModelResourceLocation(key, 1, LockModels.getModel(key.getRegistryName()));
        ModelLoader.setCustomModelResourceLocation(skeletalKey, 0, LockModels.getModel(skeletalKey.getRegistryName()));
    }
}
