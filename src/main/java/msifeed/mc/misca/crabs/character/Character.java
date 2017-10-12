package msifeed.mc.misca.crabs.character;

import net.minecraft.nbt.NBTTagCompound;

import java.io.Serializable;
import java.util.EnumMap;

public class Character implements Serializable {
    public String name = "";
    public EnumMap<Stats, Integer> stats = new EnumMap<>(Stats.class);

    public Character() {
    }

    public Character(NBTTagCompound tag) {
        fromNBT(tag);
    }

    public int stat(Stats s) {
        return stats.getOrDefault(s, 0);
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("name", name);
        for (Stats s : Stats.values()) {
            tag.setInteger(s.toString(), stats.getOrDefault(s, 0));
        }
        return tag;
    }

    public void fromNBT(NBTTagCompound tag) {
        name = tag.getString("name");
        for (Stats s : Stats.values()) {
            stats.put(s, tag.getInteger(s.toString()));
        }
    }
}
