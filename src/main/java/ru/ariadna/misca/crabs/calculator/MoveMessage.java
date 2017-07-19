package ru.ariadna.misca.crabs.calculator;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
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

    public static void sendActionRollResult(EntityPlayer player, CalcResult result) {
        String msg;
        if (result.mod != 0)
            msg = String.format("\u00A76[Roll] %s: %s [%s]+%d+%d=%d", player.getDisplayName(), result.action.toPrettyString(), makeCritical(result.dice), result.stats, result.mod, result.result);
        else
            msg = String.format("\u00A76[Roll] %s: %s [%s]+%d=%d", player.getDisplayName(), result.action.toPrettyString(), makeCritical(result.dice), result.stats, result.result);

        send(msg, player);
    }

    public static void sendCustomRollResult(EntityPlayer player, int dice, int mod) {
        String msg = String.format("\u00A76[Roll Custom] %s: [%s]+%d=%d", player.getDisplayName(), makeCritical(dice), mod, dice + mod);
        send(msg, player);
    }

    private static String makeCritical(int dice) {
        if (dice <= 3) return "\u00A74" + dice + "\u00A76";
        else if (dice >= 18) return "\u00A72" + dice + "\u00A76";
        else return String.valueOf(dice);
    }

    private static void send(String msg, EntityPlayer center) {
        center.getEntityWorld().playerEntities.stream()
                .filter(p -> ((EntityPlayer) p).getDistanceToEntity(center) <= 15)
                .forEach(o -> ((EntityPlayerMP) o).addChatMessage(new ChatComponentText(msg)));
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
                CalcResult result = Calculon.calc_fight(message.character, message.actionType, message.mod);
                sendActionRollResult(ctx.getServerHandler().playerEntity, result);
                break;
            case CUSTOM:
                sendCustomRollResult(ctx.getServerHandler().playerEntity, Calculon.roll_stat(), message.mod);
                break;
        }

        return null;
    }

    public enum Type {
        ACTION, CUSTOM
    }
}
