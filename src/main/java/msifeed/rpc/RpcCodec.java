package msifeed.rpc;

import com.google.common.primitives.Primitives;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum RpcCodec {
    INSTANCE;

    private final HashMap<Integer, TypeCodec<?>> codecsById = new HashMap<>();
    private final HashMap<Class<?>, TypeCodec<?>> codecsByType = new HashMap<>();

    RpcCodec() {
        addType(Boolean.class, ByteBuf::writeBoolean, ByteBuf::readBoolean);
        addType(Byte.class, (BiConsumer<ByteBuf, Byte>) ByteBuf::writeByte, ByteBuf::readByte);
        addType(Short.class, (BiConsumer<ByteBuf, Short>) ByteBuf::writeShort, ByteBuf::readShort);
        addType(Integer.class, ByteBuf::writeInt, ByteBuf::readInt);
        addType(Long.class, ByteBuf::writeLong, ByteBuf::readLong);
        addType(Float.class, ByteBuf::writeFloat, ByteBuf::readFloat);
        addType(Double.class, ByteBuf::writeDouble, ByteBuf::readDouble);
        addType(String.class, ByteBufUtils::writeUTF8String, ByteBufUtils::readUTF8String);
        addType(NBTTagCompound.class, ByteBufUtils::writeTag, ByteBufUtils::readTag);
        addType(ITextComponent.class,
                (buf, comp) -> ByteBufUtils.writeUTF8String(buf, ITextComponent.Serializer.componentToJson(comp)),
                buf -> ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(buf)));
    }

    public boolean hasCodec(Class<?> c) {
        return codecsByType.containsKey(c);
    }

    public <T> void encode(ByteBuf buf, T obj) {
        final Class<?> objType = obj.getClass();
        final TypeCodec<T> codec = (TypeCodec<T>) codecsByType.get(objType);
        if (codec == null)
            throw new RuntimeException(String.format("Unknown codec for type '%s'", objType.getName()));
        if (!codec.type.isAssignableFrom(objType))
            throw new RuntimeException(String.format("Tried to encode '%s' with codec for '%s'", objType.getName(), codec.type.getName()));
        buf.writeByte(codec.id);
        codec.encoder.accept(buf, obj);
    }

    public <T> T decode(ByteBuf buf) {
        final int id = buf.readByte();
        final TypeCodec<T> codec = (TypeCodec<T>) codecsById.get(id);
        if (codec == null)
            throw new RuntimeException(String.format("Unknown codec for id '%d'", id));
        return codec.decoder.apply(buf);
    }

    public <T> void addType(Class<T> type, BiConsumer<ByteBuf, T> encoder, Function<ByteBuf, T> decoder) {
        final int id = codecsById.size();
        final TypeCodec<T> codec = new TypeCodec<>(id, type, encoder, decoder);
        codecsById.put(id, codec);
        codecsByType.put(type, codec);

        if (Primitives.isWrapperType(type))
            codecsByType.put(Primitives.unwrap(type), codec);
    }

    static class TypeCodec<T> {
        public final int id;
        public final Class<T> type;
        public final BiConsumer<ByteBuf, T> encoder;
        public final Function<ByteBuf, T> decoder;

        TypeCodec(int id, Class<T> type, BiConsumer<ByteBuf, T> enc, Function<ByteBuf, T> dec) {
            this.id = id;
            this.type = type;
            this.encoder = enc;
            this.decoder = dec;
        }
    }
}
