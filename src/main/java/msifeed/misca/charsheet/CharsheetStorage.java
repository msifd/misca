package msifeed.misca.charsheet;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class CharsheetStorage implements Capability.IStorage<ICharsheet> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ICharsheet> capability, ICharsheet instance, EnumFacing side) {
        final NBTTagCompound nbt = new NBTTagCompound();

        instance.attrs().writeNBT(nbt, Tag.attributes);

        if (instance.isPlayer()) {
            nbt.setBoolean("IsPlayer", true);
            nbt.setString(Tag.name, instance.getName());
            nbt.setString(Tag.wikiPage, instance.getWikiPage());
            instance.resources().writeNBT(nbt, Tag.resources);
        }

        return nbt;
    }

    @Override
    public void readNBT(Capability<ICharsheet> capability, ICharsheet instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;

        instance.attrs().readNBT(nbt, Tag.attributes);

        if (nbt.hasKey("IsPlayer")) {
            instance.setName(nbt.getString(Tag.name));
            instance.setWikiPage(nbt.getString(Tag.wikiPage));
            instance.resources().readNBT(nbt, Tag.resources);
        }
    }

    private static class Tag {
        private static final String name = "name";
        private static final String wikiPage = "wiki";
        private static final String attributes = "attributes";
        private static final String resources = "resources";
    }
}
