package msifeed.mc.config;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

import java.io.*;
import java.util.HashMap;

public class ConfigSyncMessage implements IMessage {
    HashMap<String, Serializable> configs;

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            int size = buf.readInt();
            ByteBuf map_buf = buf.readBytes(size);

            ByteArrayInputStream bis = new ByteArrayInputStream(map_buf.array());
            ObjectInputStream ois = new ObjectInputStream(bis);
            configs = (HashMap<String, Serializable>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(configs);
            byte[] map_bytes = bos.toByteArray();

            buf.writeInt(map_bytes.length);
            buf.writeBytes(map_bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
