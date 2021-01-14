package msifeed.misca.needs.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PlayerNeedsStorage implements Capability.IStorage<IPlayerNeeds> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IPlayerNeeds> capability, IPlayerNeeds instance, EnumFacing side) {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("UpdateTime", instance.getUpdateTime());
        nbt.setLong("MiningTime", instance.getMiningTime());
        return nbt;
    }

    @Override
    public void readNBT(Capability<IPlayerNeeds> capability, IPlayerNeeds instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;
        instance.setUpdateTime(nbt.getLong("UpdateTime"));
        instance.setMiningTime(nbt.getLong("MiningTime"));
    }
}
