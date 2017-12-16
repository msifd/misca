package msifeed.mc.misca.utils;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

public class NetUtils {
    public static String readString(ByteBuf buf) {
        final int len = buf.readInt();
        return new String(buf.readBytes(len).array());
    }

    public static void writeString(ByteBuf buf, String str) {
        buf.writeInt(str.length());
        buf.writeBytes(str.getBytes(Charsets.UTF_8));
    }
}
