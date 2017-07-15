package ru.ariadna.misca.crabs.characters;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ru.ariadna.misca.crabs.Crabs;

import java.io.*;
import java.util.Optional;

public class CharacterMessage implements IMessage, IMessageHandler<CharacterMessage, CharacterMessage> {
    public Type type;
    public String name;
    public Character character;
    public boolean canEdit;

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readInt()];

        int size = buf.readInt();
        ByteBuf map_buf = buf.readBytes(size);

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(map_buf.array());
            ObjectInputStream ois = new ObjectInputStream(bis);

            switch (type) {
                case REQUEST:
                    name = (String) ois.readObject();
                    break;
                case RESPONSE:
                    name = (String) ois.readObject();
                    canEdit = ois.readBoolean();
                    character = (Character) ois.readObject();
                    break;
                case UPDATE:
                    character = (Character) ois.readObject();
                    break;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            switch (type) {
                case REQUEST:
                    oos.writeObject(name);
                    break;
                case RESPONSE:
                    oos.writeObject(name);
                    oos.writeBoolean(canEdit);
                    oos.writeObject(character);
                    break;
                case UPDATE:
                    oos.writeObject(character);
                    break;
            }

            byte[] bytes = bos.toByteArray();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CharacterMessage onMessage(CharacterMessage message, MessageContext ctx) {
        switch (message.type) {
            case REQUEST:
                Character c = Crabs.instance.characterProvider.get(message.name);
                CharacterMessage response = new CharacterMessage();
                response.type = Type.RESPONSE;
                response.name = message.name;
                response.character = c;
                response.canEdit = CharacterProvider.canEditCharacter(ctx.getServerHandler().playerEntity, name);
                return response;
            case UPDATE:
                Crabs.instance.characterProvider.update(ctx.getServerHandler().playerEntity, message.character);
                break;
            case RESPONSE:
                Crabs.instance.characterProvider.response(message.name, Optional.ofNullable(message.character));
                break;
        }
        return null;
    }

    public enum Type {
        REQUEST, UPDATE, RESPONSE
    }
}
