package msifeed.mc.aorta.genesis.blocks.templates;

import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.aorta.genesis.GenesisCreativeTab;
import msifeed.mc.aorta.genesis.blocks.BlockGenesisUnit;
import msifeed.mc.aorta.genesis.blocks.BlockTraitCommons;
import msifeed.mc.aorta.genesis.blocks.SpecialBlockRegisterer;
import msifeed.mc.aorta.genesis.blocks.client.GenesisChestRenderer;
import msifeed.mc.aorta.locks.LockObject;
import msifeed.mc.aorta.locks.LockType;
import msifeed.mc.aorta.locks.Lockable;
import msifeed.mc.aorta.locks.LockableBlock;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class ChestTemplate extends BlockChest implements SpecialBlockRegisterer, BlockTraitCommons.Getter, LockableBlock {
    private BlockTraitCommons traits;

    public ChestTemplate(BlockGenesisUnit unit) {
        super(0);
        traits = new BlockTraitCommons(unit);
        setBlockName(unit.id);
        setHardness(2.5F);
        setStepSound(soundTypeWood);
    }

    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new ChestEntity();
    }

    @Override
    public int getRenderType() {
        return GenesisChestRenderer.RENDER_ID;
    }

    @Override
    public String getTextureName() {
        return super.getTextureName();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        final LockObject lock = getLock(world, x, y, z);
        if (lock == null)
            return false;

        if (player.isSneaking() && lock.getLockType() == LockType.DIGITAL) {
            // FIXME: Aorta.GUI_HANDLER.toggleDigitalLock(lock);
            return true;
        } else if (lock.isLocked()) {
            return true;
        }

        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }

    @Override
    public BlockTraitCommons getCommons() {
        return traits;
    }

    @Override
    public void register(String id) {
        setCreativeTab(GenesisCreativeTab.BLOCKS);
        GameRegistry.registerBlock(this, ItemBlockTemplate.class, id);
    }

    @Override
    public boolean dropLockOnBreak(World world, int x, int y, int z) {
        final TileEntity targetTile = world.getTileEntity(x, y, z);
        if (!(targetTile instanceof ChestEntity))
            return false;
        final ChestEntity targetChest = (ChestEntity) targetTile;

        targetChest.checkForAdjacentChests();

        // single block chest - drop lock
        // double block chest - keep lock
        return targetChest.adjacentChestXNeg == null
            && targetChest.adjacentChestXPos == null
            && targetChest.adjacentChestZNeg == null
            && targetChest.adjacentChestZPos == null;
    }

    public static class ChestEntity extends TileEntityChest implements Lockable {
        public static final String ID = "aorta.genesis.chest_tile";
        private final LockObject lock = new LockObject(this);

        @Override
        public void markDirty() {
            super.markDirty();
            copyLockTo(adjacentChestXNeg);
            copyLockTo(adjacentChestXPos);
            copyLockTo(adjacentChestZNeg);
            copyLockTo(adjacentChestZPos);
        }

        private void copyLockTo(TileEntityChest tileEntityChest) {
            if (!(tileEntityChest instanceof ChestEntity))
                return;
            final ChestEntity e = (ChestEntity) tileEntityChest;
            if (!e.lock.equals(lock))
                e.lock.copyFrom(lock);
        }

        @Override
        public void checkForAdjacentChests() {
            super.checkForAdjacentChests();
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);
            lock.readFromNBT(compound);
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);
            lock.writeToNBT(compound);
        }

        @Override
        public Packet getDescriptionPacket() {
            final NBTTagCompound compound = new NBTTagCompound();
            writeToNBT(compound);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, compound);
        }

        @Override
        public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
            readFromNBT(packet.func_148857_g());
        }

        @Override
        public LockObject getLock() {
            return lock;
        }
    }
}
