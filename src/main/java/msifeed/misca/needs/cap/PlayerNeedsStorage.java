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
        nbt.setDouble("Integrity", instance.get(IPlayerNeeds.NeedType.integrity));
        nbt.setDouble("Sanity", instance.get(IPlayerNeeds.NeedType.sanity));
        nbt.setDouble("Stamina", instance.get(IPlayerNeeds.NeedType.stamina));
        nbt.setDouble("Corruption", instance.get(IPlayerNeeds.NeedType.corruption));
        nbt.setLong("UpdateTime", instance.getUpdateTime());
        nbt.setLong("MiningTime", instance.getMiningTime());
        return nbt;
    }

    @Override
    public void readNBT(Capability<IPlayerNeeds> capability, IPlayerNeeds instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;
        instance.set(IPlayerNeeds.NeedType.integrity, nbt.getDouble("Integrity"));
        instance.set(IPlayerNeeds.NeedType.sanity, nbt.getDouble("Sanity"));
        instance.set(IPlayerNeeds.NeedType.stamina, nbt.getDouble("Stamina"));
        instance.set(IPlayerNeeds.NeedType.corruption, nbt.getDouble("Corruption"));
        instance.setUpdateTime(nbt.getLong("UpdateTime"));
        instance.setMiningTime(nbt.getLong("MiningTime"));
    }
}
