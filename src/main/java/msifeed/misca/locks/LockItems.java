package msifeed.misca.locks;

import msifeed.misca.MiscaThings;
import msifeed.misca.locks.items.ItemBlankKey;
import msifeed.misca.locks.items.ItemKey;
import msifeed.misca.locks.items.ItemLock;
import msifeed.misca.locks.items.ItemSkeletalKey;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.stream.Stream;

public class LockItems {
    public static final ItemLock lockMechanical = new ItemLock(LockType.mechanical);
    public static final ItemLock lockDigital = new ItemLock(LockType.digital);
    public static final ItemLock lockMagical = new ItemLock(LockType.magical);

    public static final ItemKey key = new ItemKey();
    public static final ItemBlankKey blankKey = new ItemBlankKey();
    public static final ItemSkeletalKey skeletalKey = new ItemSkeletalKey();

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                lockMechanical,
                lockDigital,
                lockMagical,
                key,
                blankKey,
                skeletalKey
        );
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        Stream.of(
                lockMechanical,
                lockDigital,
                lockMagical,
                key,
                blankKey,
                skeletalKey
        ).forEach(MiscaThings::registerItemModel);
    }
}
