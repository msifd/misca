package ru.ariadna.misca.crabs.lobby;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ru.ariadna.misca.crabs.Crabs;

/**
 * Сообщение получает только сервер
 */
public class LobbyActionMessage implements IMessage, IMessageHandler<LobbyActionMessage, IMessage> {
    public Type type;
    public String name;
    public int entityId;

    public LobbyActionMessage() {
        this.type = Type.NOOP;
    }

    public LobbyActionMessage(Type type) {
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readByte()];
        switch (type) {
            case JOIN:
            case INCLUDE:
                int len = buf.readInt();
                byte[] bytes = buf.readBytes(len).array();
                name = new String(bytes, Charsets.UTF_8);
                break;
            case EXCLUDE:
                entityId = buf.readInt();
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        switch (type) {
            case JOIN:
            case INCLUDE:
                byte[] bytes = name.getBytes(Charsets.UTF_8);
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
                break;
            case EXCLUDE:
                buf.writeInt(entityId);
                break;
        }
    }

    @Override
    public IMessage onMessage(LobbyActionMessage message, MessageContext ctx) {
        switch (message.type) {
            case NOOP:
                Crabs.instance.lobbyManager.updatePlayersLobby(ctx.getServerHandler().playerEntity);
                break;
            case CREATE:
                Crabs.instance.lobbyManager.createLobby(ctx.getServerHandler().playerEntity);
                break;
            case JOIN:
                Crabs.instance.lobbyManager.joinToPlayer(ctx.getServerHandler().playerEntity, message.name);
                break;
            case LEAVE:
                Crabs.instance.lobbyManager.leaveLobby(ctx.getServerHandler().playerEntity);
                break;
            case FIGHT:
                Crabs.instance.lobbyManager.startFight(ctx.getServerHandler().playerEntity);
                break;
        }
        return null;
    }

    public enum Type {
        NOOP, CREATE, JOIN, LEAVE, INCLUDE, EXCLUDE, FIGHT
    }
}
