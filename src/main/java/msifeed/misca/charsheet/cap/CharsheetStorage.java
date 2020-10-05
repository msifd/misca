package msifeed.misca.charsheet.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class CharsheetStorage implements Capability.IStorage<ICharsheet> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ICharsheet> capability, ICharsheet instance, EnumFacing side) {
        final NBTTagCompound nbt = new NBTTagCompound();

        nbt.setString(Tag.name, instance.getName());
        nbt.setIntArray(Tag.abilities, Stream.of(CharAbility.values()).mapToInt(instance::getAbility).toArray());
        nbt.setIntArray(Tag.counters, Stream.of(CharCounter.values()).mapToInt(instance::getCounter).toArray());

        return nbt;
    }

    @Override
    public void readNBT(Capability<ICharsheet> capability, ICharsheet instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;

        instance.setName(nbt.getString(Tag.name));

        final int[] abilitiesArr = nbt.getIntArray(Tag.abilities);
        for (CharAbility abi : CharAbility.values())
            instance.setAbility(abi, abilitiesArr[abi.ordinal()]);

        final int[] countersArr = nbt.getIntArray(Tag.counters);
        for (CharCounter ctr : CharCounter.values())
            instance.setCounter(ctr, countersArr[ctr.ordinal()]);
    }

    private static class Tag {
        private static final String name = "name";
        private static final String abilities = "abilities";
        private static final String counters = "counters";
    }
}
