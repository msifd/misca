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

        nbt.setIntArray(Tag.attributes, Stream.of(CharAttribute.values()).mapToInt(instance::getAttribute).toArray());

        if (instance.isPlayer()) {
            nbt.setBoolean("IsPlayer", true);
            nbt.setString(Tag.name, instance.getName());
            nbt.setIntArray(Tag.counters, Stream.of(CharCounter.values()).mapToInt(instance::getCounter).toArray());
        }

        return nbt;
    }

    @Override
    public void readNBT(Capability<ICharsheet> capability, ICharsheet instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;

        final int[] attributesArr = nbt.getIntArray(Tag.attributes);
        if (attributesArr.length == CharAttribute.values().length) {
            for (CharAttribute attr : CharAttribute.values())
                instance.setAttribute(attr, attributesArr[attr.ordinal()]);
        }

        if (nbt.hasKey("IsPlayer")) {
            instance.setName(nbt.getString(Tag.name));
            final int[] countersArr = nbt.getIntArray(Tag.counters);
            if (countersArr.length == CharCounter.values().length) {
                for (CharCounter ctr : CharCounter.values())
                    instance.setCounter(ctr, countersArr[ctr.ordinal()]);
            }
        }
    }

    private static class Tag {
        private static final String name = "name";
        private static final String attributes = "attributes";
        private static final String counters = "counters";
    }
}
