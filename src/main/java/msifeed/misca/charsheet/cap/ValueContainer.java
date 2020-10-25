package msifeed.misca.charsheet.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import sun.misc.SharedSecrets;

import java.util.EnumMap;
import java.util.stream.Stream;

public class ValueContainer<K extends Enum<K>> implements Cloneable {
    private final EnumMap<K, Integer> values;

    private final K[] keys;
    private final int min;
    private final int max;

    public ValueContainer(Class<K> enumType, int min, int max) {
        this.values = new EnumMap<>(enumType);
        this.keys = SharedSecrets.getJavaLangAccess().getEnumConstantsShared(enumType);
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

    public void add(K key, int delta) {
        set(key, get(key) + delta);
    }

    public void replaceWith(ValueContainer<K> other) {
        values.clear();
        values.putAll(other.values);
    }

    public void writeNBT(NBTTagCompound nbt, String key) {
        nbt.setIntArray(key, Stream.of(keys).mapToInt(this::get).toArray());
    }

    public void readNBT(NBTTagCompound nbt, String key) {
        values.clear();

        final int[] arr = nbt.getIntArray(key);
        for (int i = 0; i < Math.min(arr.length, keys.length); i++)
            set(keys[i], arr[i]);
    }

    @Override
    public ValueContainer<K> clone() {
        final ValueContainer<K> clone;
        try {
            clone = (ValueContainer<K>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        clone.values.putAll(values);
        return clone;
    }
}
