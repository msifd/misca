package msifeed.sys.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import java.util.EnumMap;
import java.util.stream.Stream;

public class FloatContainer<K extends Enum<K>> implements Cloneable {
    private final EnumMap<K, Float> values;

    private final transient K[] keys;
    private final transient float min;
    private final transient float max;

    public FloatContainer(Class<K> enumType, float min, float max) {
        this.values = new EnumMap<>(enumType);
        this.keys = enumType.getEnumConstants();
        this.min = min;
        this.max = max;
    }

    public float get(K key) {
        return values.getOrDefault(key, min);
    }

    public void set(K key, float value) {
        values.put(key, MathHelper.clamp(value, min, max));
    }

    public void setAll(float value) {
        for (K key : keys)
            set(key, value);
    }

    public void inc(K key, float delta) {
        set(key, get(key) + delta);
    }

    public void replaceWith(FloatContainer<K> other) {
        values.clear();
        values.putAll(other.values);
    }

    public void writeNBT(String key, NBTTagCompound nbt) {
        nbt.setIntArray(key, Stream.of(keys).mapToInt(k -> Float.floatToIntBits(get(k))).toArray());
    }

    public void readNBT(NBTTagCompound nbt, String key) {
        values.clear();

        final int[] arr = nbt.getIntArray(key);
        for (int i = 0; i < Math.min(arr.length, keys.length); i++)
            set(keys[i], Float.intBitsToFloat(arr[i]));
    }

    @Override
    public FloatContainer<K> clone() {
        final FloatContainer<K> clone;
        try {
            clone = (FloatContainer<K>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        clone.values.putAll(values);
        return clone;
    }
}
