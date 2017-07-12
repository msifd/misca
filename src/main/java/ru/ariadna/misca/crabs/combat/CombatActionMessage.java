package ru.ariadna.misca.crabs.combat;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.combat.parts.Action;

import java.io.*;

/**
 * Сообщение получает только сервер
 */
public class CombatActionMessage implements IMessage, IMessageHandler<CombatActionMessage, IMessage> {
    public Type type;
    public Action action;

    public CombatActionMessage() {
        this.type = Type.NOOP;
    }

    public CombatActionMessage(Type type) {
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readInt()];

        if (type == Type.NOOP) return;

        int size = buf.readInt();
        ByteBuf map_buf = buf.readBytes(size);

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(map_buf.array());
            ObjectInputStream ois = new ObjectInputStream(bis);

            switch (type) {
                case MOVE:
                    action = (Action) ois.readObject();
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());

        if (type == Type.NOOP) return;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            switch (type) {
                case MOVE:
                    oos.writeObject(action);
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
    public IMessage onMessage(CombatActionMessage message, MessageContext ctx) {
        switch (message.type) {
            case NOOP:
                Crabs.instance.fighterManager.updatePlayersFight(ctx.getServerHandler().playerEntity);
                break;
            case MOVE:
                Crabs.instance.fighterManager.makeMove(ctx.getServerHandler().playerEntity, ctx.getServerHandler().playerEntity, message.action);
                break;
        }
        return null;
    }

    public enum Type {
        NOOP, MOVE
    }
}
