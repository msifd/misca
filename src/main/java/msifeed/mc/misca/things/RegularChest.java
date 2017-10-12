package msifeed.mc.misca.things;

import net.minecraft.block.BlockChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class RegularChest extends BlockChest {
    public static final String NAME_BASE = "misca_chest_";
    public final int index;

    RegularChest(int index) {
        super(0);
        this.index = index;

        setBlockName(NAME_BASE + index);
        setCreativeTab(MiscaThings.tab);

        setHardness(2.5F);
        setStepSound(soundTypeWood);
    }

    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        ChestEntity e = new ChestEntity();
        e.setIndex(index);
        return e;
    }

    public static class ChestEntity extends TileEntityChest {
        private int index;
        private String name_base;

        public String getNameBase() {
            return name_base;
        }

        private void setIndex(int index) {
            this.index = index;
            this.name_base = NAME_BASE + index;
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
            setIndex(tagCompound.getInteger("block_index"));
        }

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
            tagCompound.setInteger("block_index", index);
        }
    }
}
