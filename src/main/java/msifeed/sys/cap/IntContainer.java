package msifeed.sys.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

public class IntContainer<K extends Enum<K>> implements Cloneable {
    private final EnumMap<K, Integer> values;

    private final transient K[] keys;
    private final transient int min;
    private final transient int max;

    public IntContainer(Class<K> enumType, int min, int max) {
        this.values = new EnumMap<>(enumType);
        this.keys = enumType.getEnumConstants();
        this.min = min;
        this.max = max;
    }

    public int get(K key) {
        return values.getOrDefault(key, min);
    }

    public void set(K key, int value) {
        values.put(key, MathHelper.clamp(value, min, max));
    }

    public void setAll(int value) {
        for (K key : keys)
            set(key, value);
    }

    public void increase(K key, int delta) {
        set(key, get(key) + delta);
    }

    public void replaceWith(IntContainer<K> other) {
        values.clear();
        values.putAll(other.values);
    }

    public void writeNBT(String key, NBTTagCompound nbt) {
        nbt.setIntArray(key, Stream.of(keys).mapToInt(this::get).toArray());
    }

    public void readNBT(NBTTagCompound nbt, String key) {
        values.clear();

        final int[] arr = nbt.getIntArray(key);
        for (int i = 0; i < Math.min(arr.length, keys.length); i++)
            set(keys[i], arr[i]);
    }

    public Stream<Map.Entry<K, Integer>> stream() {
        return values.entrySet().stream();
    }

    @Override
    public IntContainer<K> clone() {
        final IntContainer<K> clone;
        try {
            clone = (IntContainer<K>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        clone.values.putAll(values);
        return clone;
    }
}
