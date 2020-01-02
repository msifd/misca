package msifeed.mc.aorta.locks;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.aorta.locks.items.*;
import msifeed.mc.aorta.sys.rpc.Rpc;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.RecipeSorter;

public enum Locks {
    INSTANCE;

    public static final String MODID = "misca";
    private static final HashFunction hasher = Hashing.murmur3_128(3364);
    private static final LocksRpc rpcHandler = new LocksRpc();

    public static void init() {
        LockType.locks().forEach(t -> GameRegistry.registerItem(new LockItem(t), LockItem.getItemId(t)));
        GameRegistry.registerItem(new BlankKeyItem(), BlankKeyItem.ID);
        GameRegistry.registerItem(new KeyItem(), KeyItem.ID);
        GameRegistry.registerItem(new LockpickItem(), LockpickItem.ID);
        GameRegistry.registerItem(new AdvancedLockpickItem(), AdvancedLockpickItem.ID);
        GameRegistry.registerItem(new AccessTunerItem(), AccessTunerItem.ID);
        GameRegistry.registerItem(new AdvancedAccessTunerItem(), AdvancedAccessTunerItem.ID);
        GameRegistry.registerItem(new SkeletalKeyItem(), SkeletalKeyItem.ID);
        GameRegistry.registerTileEntity(LockTileEntity.class, LockTileEntity.ID);

        GameRegistry.addRecipe(new CopyKeyRecipe());
        RecipeSorter.register(MODID + ":copy_key", CopyKeyRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

        Rpc.register(rpcHandler);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.block instanceof LockableBlock))
            return;
        final LockableBlock block = (LockableBlock) event.block;
        if (!block.dropLockOnBreak(event.world, event.x, event.y, event.z))
            return;

        final LockObject lock = block.getLock(event.world, event.x, event.y, event.z);
        if (lock != null && lock.hasLock() && !lock.isLocked())
            event.world.spawnEntityInWorld(lock.makeEntityItem());
    }

    public static int makeKeyHash(String input) {
        return hasher.hashUnencodedChars(input).asInt();
    }
}
