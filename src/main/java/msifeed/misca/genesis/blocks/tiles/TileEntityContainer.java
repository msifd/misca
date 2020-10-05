package msifeed.misca.genesis.blocks.tiles;

import msifeed.misca.Misca;
import msifeed.misca.genesis.blocks.BlockRule;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class TileEntityContainer extends TileEntityLockable {
    public static final ResourceLocation RESOURCE = new ResourceLocation(Misca.MODID, "genesis.container");

    private int capacity;
    private NonNullList<ItemStack> content;

    private int numPlayersUsing;

    public TileEntityContainer() {
        // For TileEntity.create
    }

    public TileEntityContainer(BlockRule rule) {
        this.capacity = rule.containerCapacity;
        this.content = NonNullList.<ItemStack>withSize(rule.containerCapacity, ItemStack.EMPTY);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.capacity = compound.getInteger("Capacity");
        this.content = NonNullList.withSize(capacity, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, content);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("Capacity", capacity);
        ItemStackHelper.saveAllItems(compound, content);

        return compound;
    }

    @Override
    public int getSizeInventory() {
        return capacity;
    }

    @Override
    public boolean isEmpty() {
        return content.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return content.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        final ItemStack itemstack = ItemStackHelper.getAndSplit(content, index, count);
        if (!itemstack.isEmpty())
            this.markDirty();
        return itemstack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(content, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        content.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit())
            stack.setCount(this.getInventoryStackLimit());

        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this)
            return false;
        else
            return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0)
                this.numPlayersUsing = 0;

            ++this.numPlayersUsing;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (!player.isSpectator() && this.getBlockType() instanceof BlockContainer) {
            --this.numPlayersUsing;

            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        content.clear();
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerChest(playerInventory, this, playerIn);
    }

    @Override
    public String getGuiID() {
        return "minecraft:chest";
    }

    @Override
    public String getName() {
        return "container.chest";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }
}
