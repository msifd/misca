package msifeed.misca.locks;

import msifeed.misca.Misca;
import msifeed.misca.locks.chunk.ChunkLockable;
import msifeed.misca.locks.chunk.ChunkLockableProvider;
import msifeed.misca.locks.chunk.ChunkLockableStorage;
import msifeed.misca.locks.chunk.IChunkLockable;
import msifeed.misca.locks.rpc.ILockRpc;
import msifeed.misca.locks.rpc.LocksClientRpc;
import msifeed.misca.locks.rpc.LocksServerRpc;
import msifeed.misca.locks.tile.ILockable;
import msifeed.misca.locks.tile.Lockable;
import msifeed.misca.locks.tile.LockableProvider;
import msifeed.misca.locks.tile.LockableStorage;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class Locks {
    public static final ResourceLocation CAP = new ResourceLocation(Misca.MODID, "lock");

    public void preInit() {
        CapabilityManager.INSTANCE.register(ILockable.class, new LockableStorage(), Lockable::new);
        CapabilityManager.INSTANCE.register(IChunkLockable.class, new ChunkLockableStorage(), ChunkLockable::new);
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
        Misca.RPC.register(new LocksServerRpc());

        if (FMLCommonHandler.instance().getSide().isClient())
            Misca.RPC.register(new LocksClientRpc());
    }

    @SubscribeEvent
    public void onAttachTileCapability(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof IInventory)
            event.addCapability(CAP, new LockableProvider());
    }

    @SubscribeEvent
    public void onAttachChunkCapability(AttachCapabilitiesEvent<Chunk> event) {
        event.addCapability(CAP, new ChunkLockableProvider());
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.getWorld().isRemote)
            ILockRpc.requestChunkLocks(event.getChunk());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        final ILockable lock = getLock(event.getWorld(), event.getPos());
        if (lock == null) return;

        if (lock.isLocked()) {
            event.setCanceled(true);

            final ITextComponent te = new TextComponentString("Locked!");
            te.getStyle().setColor(TextFormatting.YELLOW);
            event.getEntityPlayer().sendStatusMessage(te, true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        final ILockable lock = getLock(event.getWorld(), event.getPos());
        if (lock == null) return;

        if (lock.isLocked()) {
            event.setCanceled(true);
        } else {
            removeLock(event.getWorld(), event.getPos());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBlockBreakSpeed(PlayerEvent.BreakSpeed event) {
        final ILockable lock = getLock(event.getEntityPlayer().world, event.getPos());
        if (lock != null && lock.isLocked()) {
            event.setCanceled(true);
        }
    }

    @Nullable
    public static ILockable getLock(World world, BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            return LockableProvider.get(tile);
        } else {
            final BlockPos actualPos = getChunkLockPos(world, pos);
            if (actualPos == null) return null;
            final Chunk chunk = world.getChunkFromBlockCoords(actualPos);
            return ChunkLockableProvider.get(chunk).getLock(actualPos);
        }
    }

    public static boolean toggleLock(World world, BlockPos pos, String key) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            final ILockable lock = LockableProvider.get(tile);
            if (lock == null || !lock.hasSecret() || !lock.canOpenWith(key)) return false;

            lock.setLocked(!lock.isLocked());
            tile.markDirty();
        } else {
            final BlockPos actualPos = getChunkLockPos(world, pos);
            if (actualPos == null) return false;
            final Chunk chunk = world.getChunkFromBlockCoords(actualPos);
            final ILockable lock = ChunkLockableProvider.get(chunk).getLock(actualPos);
            if (lock == null || !lock.hasSecret() || !lock.canOpenWith(key)) return false;

            lock.setLocked(!lock.isLocked());
            chunk.markDirty();

            if (!world.isRemote)
                ILockRpc.syncChunkLocks(chunk, ChunkLockableProvider.get(chunk));
        }

        return true;
    }

    public static boolean addLock(World world, BlockPos pos, String secret) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            final ILockable lock = LockableProvider.get(tile);
            if (lock == null || lock.isLocked() || lock.hasSecret()) return false;

            lock.setLocked(false);
            lock.setSecret(secret);
            tile.markDirty();
            return true;
        } else {
            return addChunkLock(world, pos, secret);
        }
    }

    private static boolean addChunkLock(World world, BlockPos pos, String secret) {
        final BlockPos actualPos = getChunkLockPos(world, pos);
        if (actualPos == null) return false;
        final Chunk chunk = world.getChunkFromBlockCoords(actualPos);
        final IChunkLockable chunkLocks = ChunkLockableProvider.get(chunk);
        if (chunkLocks.getLock(pos) != null) return false;

        chunkLocks.addLock(actualPos, new Lockable(false, secret));
        chunk.markDirty();

        if (!world.isRemote)
            ILockRpc.syncChunkLocks(chunk, ChunkLockableProvider.get(chunk));

        return true;
    }

    public static boolean removeLock(World world, BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            final ILockable lock = LockableProvider.get(tile);
            if (lock == null || lock.isLocked()) return false;

            lock.setLocked(false);
            lock.setSecret(ILockable.NO_SECRET);
            tile.markDirty();
            return true;
        } else {
            return removeChunkLock(world, pos);
        }
    }

    private static boolean removeChunkLock(World world, BlockPos pos) {
        final BlockPos actualPos = getChunkLockPos(world, pos);
        if (actualPos == null) return false;
        final Chunk chunk = world.getChunkFromBlockCoords(actualPos);

        final boolean removed = ChunkLockableProvider.get(chunk).removeLock(actualPos);
        if (removed) {
            chunk.markDirty();
            if (!world.isRemote)
                ILockRpc.syncChunkLocks(chunk, ChunkLockableProvider.get(chunk));
        }

        return removed;
    }

    @Nullable
    private static BlockPos getChunkLockPos(World world, BlockPos pos) {
        final IBlockState state = world.getBlockState(pos);
        final ResourceLocation blockId = state.getBlock().getRegistryName();
        final LocksConfig.Lookup lookup = Misca.getSharedConfig().locks.tileless.get(blockId);
        if (lookup == null) return null;

        if (lookup == LocksConfig.Lookup.door) {
            if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER)
                return pos.down();
            else
                return pos;
        } else {
            return pos;
        }
    }
}
