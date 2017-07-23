package ru.ariadna.misca.crabs.calculator;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ru.ariadna.misca.crabs.characters.CharStats;
import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.crabs.combat.parts.ActionType;

import java.io.*;

public class MoveMessage implements IMessage, IMessageHandler<MoveMessage, IMessage> {
    public Type type;
    public Character character;
    public ActionType actionType;
    public int mod;
    public CharStats stat;
    public int stat_value;

    public MoveMessage() {

    }

    public MoveMessage(Type type) {
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readInt()];

        if (type == Type.CUSTOM) {
            stat = CharStats.values()[buf.readInt()];
            stat_value = buf.readInt();
            mod = buf.readInt();
            return;
        }

        try {
            int len = buf.readInt();
            ByteBuf bytes = buf.readBytes(len);

            ByteArrayInputStream bis = new ByteArrayInputStream(bytes.array());
            ObjectInputStream oos = new ObjectInputStream(bis);
            mod = oos.readInt();
            character = (Character) oos.readObject();
            actionType = (ActionType) oos.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());

        if (type == Type.CUSTOM) {
            buf.writeInt(stat.ordinal());
            buf.writeInt(stat_value);
            buf.writeInt(mod);
            return;
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeInt(mod);
            oos.writeObject(character);
            oos.writeObject(actionType);

            byte[] bytes = bos.toByteArray();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IMessage onMessage(MoveMessage message, MessageContext ctx) {
        switch (message.type) {
            case ACTION:
                CalcResult result = Calculon.calc_fight(message.character, message.actionType, message.mod);
                RollPrinter.sendActionRollResult(ctx.getServerHandler().playerEntity, result);
                break;
            case CUSTOM:
                RollPrinter.sendCustomRollResult(ctx.getServerHandler().playerEntity, message.stat, Calculon.roll_stat(), message.stat_value, message.mod);
                break;
        }

        return null;
    }

    public enum Type {
        ACTION, CUSTOM
    }
}
