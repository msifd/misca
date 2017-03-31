package ru.ariadna.misca.charsheet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import org.apache.commons.io.Charsets;

public class CharsheetMessage implements IMessage {
    Type type;
    String payload;

    public CharsheetMessage() {
    }

    public CharsheetMessage(Type type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.type = Type.values()[buf.readByte()];
        if (type != Type.CLEAR) {
            this.payload = buf.toString(Charsets.UTF_8);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        if (type != Type.CLEAR) {
            buf.writeBytes(payload.getBytes(Charsets.UTF_8));
        }
    }

    public enum Type {
        GET, SET, CLEAR
    }
}
