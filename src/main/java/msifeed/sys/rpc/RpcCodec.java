package msifeed.sys.rpc;

import com.google.common.primitives.Primitives;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class RpcCodec {
    private final HashMap<Class<?>, TypeCodec<?>> codecsByType = new HashMap<>();

    public RpcCodec() {
        addType(Boolean.class, ByteBuf::writeBoolean, ByteBuf::readBoolean);
        addType(Byte.class, (BiConsumer<ByteBuf, Byte>) ByteBuf::writeByte, ByteBuf::readByte);
        addType(Short.class, (BiConsumer<ByteBuf, Short>) ByteBuf::writeShort, ByteBuf::readShort);
        addType(Integer.class, ByteBuf::writeInt, ByteBuf::readInt);
        addType(Long.class, ByteBuf::writeLong, ByteBuf::readLong);
        addType(Float.class, ByteBuf::writeFloat, ByteBuf::readFloat);
        addType(Double.class, ByteBuf::writeDouble, ByteBuf::readDouble);
        addType(String.class, ByteBufUtils::writeUTF8String, ByteBufUtils::readUTF8String);
        addType(NBTTagCompound.class, ByteBufUtils::writeTag, ByteBufUtils::readTag);
        addType(UUID.class,
                (buf, uuid) -> {
                    buf.writeLong(uuid.getMostSignificantBits());
                    buf.writeLong(uuid.getLeastSignificantBits());
                },
                buf -> new UUID(buf.readLong(), buf.readLong()));
        addType(BlockPos.class,
                (buf, pos) -> buf.writeLong(pos.toLong()),
                buf -> BlockPos.fromLong(buf.readLong()));

        addType(byte[].class, RpcCodec::writeCompressed, RpcCodec::readCompressed);

        addType(ITextComponent.class,
                (buf, comp) -> ByteBufUtils.writeUTF8String(buf, ITextComponent.Serializer.componentToJson(comp)),
                buf -> ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(buf)));
        addAlias(TextComponentBase.class, ITextComponent.class);
        addAlias(TextComponentKeybind.class, ITextComponent.class);
        addAlias(TextComponentScore.class, ITextComponent.class);
        addAlias(TextComponentSelector.class, ITextComponent.class);
        addAlias(TextComponentString.class, ITextComponent.class);
        addAlias(TextComponentTranslation.class, ITextComponent.class);
    }

    public boolean hasCodecForType(Class<?> c) {
        return codecsByType.containsKey(c);
    }

    public <T> void encode(ByteBuf buf, T obj) {
        final Class<?> objType = obj.getClass();
        final TypeCodec<T> codec = (TypeCodec<T>) codecsByType.get(objType);
        if (codec == null)
            throw new RuntimeException(String.format("Unknown codec for type '%s'", objType.getName()));
        codec.encoder.accept(buf, obj);
    }

    public <T> Object decode(Class<T> type, ByteBuf buf) {
        return codecsByType.get(type).decoder.apply(buf);
    }

    public <T> void addType(Class<T> type, BiConsumer<ByteBuf, T> encoder, Function<ByteBuf, T> decoder) {
        if (hasCodecForType(type))
            throw new RuntimeException(String.format("Duplicate codec for type '%s'", type.getName()));

        final TypeCodec<T> codec = new TypeCodec<>(type, encoder, decoder);
        codecsByType.put(type, codec);

        if (Primitives.isWrapperType(type))
            codecsByType.put(Primitives.unwrap(type), codec);
    }

    public <T> void addAlias(Class<? extends T> type, Class<T> alias) {
        if (hasCodecForType(type))
            throw new RuntimeException(String.format("Duplicate codec for type '%s'", type.getName()));

        final TypeCodec<T> codec = (TypeCodec<T>) codecsByType.get(alias);
        if (codec == null)
            throw new RuntimeException(String.format("Unknown codec for alias '%s'", alias.getName()));

        codecsByType.put(type, codec);
    }

    private static void writeCompressed(ByteBuf buf, byte[] bb) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final DeflaterOutputStream out = new DeflaterOutputStream(bos);
            out.write(bb);
            out.close();

            buf.writeInt(bos.size());
            buf.writeBytes(bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("failed to deflate bytes");
        }
    }

    private static byte[] readCompressed(ByteBuf buf) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final InflaterOutputStream out = new InflaterOutputStream(bos);
            buf.readBytes(out, buf.readInt());
            out.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("failed to inflate bytes");
        }
    }

    private static class TypeCodec<T> {
        public final Class<T> type;
        public final BiConsumer<ByteBuf, T> encoder;
        public final Function<ByteBuf, T> decoder;

        TypeCodec(Class<T> type, BiConsumer<ByteBuf, T> enc, Function<ByteBuf, T> dec) {
            this.type = type;
            this.encoder = enc;
            this.decoder = dec;
        }
    }
}
