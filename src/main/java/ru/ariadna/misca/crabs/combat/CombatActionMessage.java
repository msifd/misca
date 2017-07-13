package ru.ariadna.misca.crabs.combat;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.combat.parts.Action;

import java.io.*;

/**
 * Сообщение получает только сервер
 */
public class CombatActionMessage implements IMessage, IMessageHandler<CombatActionMessage, IMessage> {
    public Type type;
    public int targetId;
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

        if (type != Type.UPDATE) return;

        int size = buf.readInt();
        ByteBuf map_buf = buf.readBytes(size);

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(map_buf.array());
            ObjectInputStream ois = new ObjectInputStream(bis);

            targetId = ois.readInt();
            action = (Action) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());

        if (type != Type.UPDATE) return;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeInt(targetId);
            oos.writeObject(action);

            byte[] bytes = bos.toByteArray();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IMessage onMessage(CombatActionMessage msg, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        switch (msg.type) {
            case NOOP:
                Crabs.instance.fighterManager.updatePlayersFight(player);
                break;
            case UPDATE:
                Crabs.instance.fighterManager.updateAction(player, msg.targetId, msg.action);
                break;
            case MOVE:
                Crabs.instance.fighterManager.makeMove(player);
                break;
            case END:
                Crabs.instance.fighterManager.endFight(player);
                break;
        }
        return null;
    }

    public enum Type {
        NOOP, UPDATE, MOVE, END
    }
}
