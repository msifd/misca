package msifeed.misca.needs.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class AbsoluteTimeStorage implements Capability.IStorage<IAbsoluteTime> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IAbsoluteTime> capability, IAbsoluteTime instance, EnumFacing side) {
        return new NBTTagLong(instance.getTime());
    }

    @Override
    public void readNBT(Capability<IAbsoluteTime> capability, IAbsoluteTime instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagLong tag = (NBTTagLong) nbtBase;
        instance.setTime(tag.getLong());
    }
}
