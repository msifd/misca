package msifeed.mc.misca.crabs.character;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.utils.AbstractMessage;

import java.util.UUID;

public class CharacterMessage extends AbstractMessage<CharacterMessage> {
    private Type type;
    UUID uuid;
    Character character;

    public CharacterMessage() {
    }

    public CharacterMessage(UUID uuid) {
        this.type = Type.GET;
        this.uuid = uuid;
    }

    public CharacterMessage(UUID uuid, Character character) {
        this.type = Type.POST;
        this.uuid = uuid;
        this.character = character;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readByte()];
        uuid = UUID.fromString(readShortString(buf));

        if (type == Type.POST) {
            character = new Character();
            character.name = readShortString(buf);
            character.isPlayer = buf.readBoolean();
            for (Stats s : Stats.values())
                character.stats.put(s, (int) buf.readByte());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        writeShortString(buf, uuid.toString());

        if (type == Type.POST) {
            writeShortString(buf, character.name);
            buf.writeBoolean(character.isPlayer);
            for (Stats s : Stats.values())
                buf.writeByte(character.stat(s));
        }
    }

    @Override
    public CharacterMessage onMessage(CharacterMessage message, MessageContext ctx) {
        switch (message.type) {
            case GET:
                final Character c = CharacterManager.INSTANCE.get(message.uuid);
                return new CharacterMessage(message.uuid, c);
            case POST:
                if (ctx.side.isServer()) CharacterManager.INSTANCE.onServerReceive(ctx.getServerHandler().playerEntity, message);
                else CharacterManager.INSTANCE.onFetchResponse(message);
            default:
                return null;
        }
    }

    private enum Type {
        GET, POST
    }
}
