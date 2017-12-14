package msifeed.mc.misca.crabs.character;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class CharacterMessage implements IMessage, IMessageHandler<CharacterMessage, IMessage> {
    Type type;
    UUID uuid;
    Character character;

    public CharacterMessage() {
    }

    public CharacterMessage(UUID uuid) {
        this.type = Type.REQUEST;
        this.uuid = uuid;
    }

    public CharacterMessage(UUID uuid, Character character) {
        this.type = Type.RESPONSE;
        this.uuid = uuid;
        this.character = character;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readByte()];

        final byte uuidStrLen = buf.readByte();
        final String uuidStr = new String(buf.readBytes(uuidStrLen).array());
        uuid = UUID.fromString(uuidStr);

        if (type == Type.REQUEST) return;
        character = new Character();
        for (Stats s : Stats.values()) {
            character.stats.put(s, (int) buf.readByte());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());

        final String uuidStr = uuid.toString();
        buf.writeByte(uuidStr.length());
        buf.writeBytes(uuidStr.getBytes(Charsets.UTF_8));

        if (type == Type.REQUEST) return;
        for (Stats s : Stats.values()) {
            buf.writeByte(character.stat(s));
        }
    }

    @Override
    public IMessage onMessage(CharacterMessage message, MessageContext ctx) {
        CharacterManager.INSTANCE.onCharacterReceive(message);
        return null;
    }

    private enum Type {
        REQUEST, RESPONSE
    }
}
