package ru.ariadna.misca.crabs.calculator;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.crabs.combat.parts.ActionType;

import java.io.*;

public class MoveMessage implements IMessage, IMessageHandler<MoveMessage, IMessage> {
    public Type type;
    public Character character;
    public ActionType actionType;
    public int mod;

    public MoveMessage() {

    }

    public MoveMessage(Type type) {
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readInt()];

        if (type == Type.CUSTOM) {
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
                calc_action(message, ctx);
                break;
            case CUSTOM:
                calc_custom(message, ctx);
                break;
        }

        return null;
    }

    private void calc_action(MoveMessage message, MessageContext ctx) {
        CalcResult result = Calculon.calc_fight(message.character, message.actionType, message.mod);
        String msg;
        if (message.mod != 0)
            msg = String.format("[Roll] %s: %s [%d]+%d+%d=%d", ctx.getServerHandler().playerEntity.getDisplayName(), stringifyAction(message.actionType), result.dice, result.stats, result.mod, result.result);
        else
            msg = String.format("[Roll] %s: %s [%d]+%d=%d", ctx.getServerHandler().playerEntity.getDisplayName(), stringifyAction(message.actionType), result.dice, result.stats, result.result);

        send(msg, ctx.getServerHandler().playerEntity);
    }

    private void calc_custom(MoveMessage message, MessageContext ctx) {
        int dice = Calculon.roll_d10();
        String msg = String.format("[Roll Custom] %s: [%d]+%d=%d", ctx.getServerHandler().playerEntity.getDisplayName(), dice, message.mod, dice + message.mod);
        send(msg, ctx.getServerHandler().playerEntity);
    }

    private String stringifyAction(ActionType actionType) {
        switch (actionType) {
            case PHYSICAL:
                return MiscaUtils.localize("misca.calculator.action.phys");
            case SHOOT:
                return MiscaUtils.localize("misca.calculator.action.shoot");
            case DEFEND:
                return MiscaUtils.localize("misca.calculator.action.def");
            case MAGIC:
                return MiscaUtils.localize("misca.calculator.action.magic");
            default:
                return "Death rays!";
        }
    }

    private void send(String msg, EntityPlayer center) {
        center.getEntityWorld().playerEntities.stream()
                .filter(p -> ((EntityPlayer) p).getDistanceToEntity(center) <= 15)
                .forEach(o -> ((EntityPlayerMP) o).addChatMessage(new ChatComponentText(msg)));
    }

    public enum Type {
        ACTION, CUSTOM
    }
}
