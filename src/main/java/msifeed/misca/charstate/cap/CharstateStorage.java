package msifeed.misca.charstate.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class CharstateStorage implements Capability.IStorage<ICharstate> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ICharstate> capability, ICharstate instance, EnumFacing side) {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("UpdateTime", instance.getUpdateTime());
        nbt.setLong("MiningTime", instance.getMiningTime());
        nbt.setLong("SilenceTime", instance.getSilenceTime());
        nbt.setInteger("Nonce", instance.nonce());

        instance.efforts().writeNBT("Efforts", nbt);
        instance.tolerances().writeNBT("Tolerances", nbt);
        return nbt;
    }

    @Override
    public void readNBT(Capability<ICharstate> capability, ICharstate instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;
        instance.setUpdateTime(nbt.getLong("UpdateTime"));
        instance.setMiningTime(nbt.getLong("MiningTime"));
        instance.setSilenceTime(nbt.getLong("SilenceTime"));
        instance.setNonce(nbt.getInteger("Nonce"));

        instance.efforts().readNBT(nbt, "Efforts");
        instance.tolerances().readNBT(nbt, "Tolerances");
    }
}
