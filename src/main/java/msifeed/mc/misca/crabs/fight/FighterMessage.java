package msifeed.mc.misca.crabs.fight;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.crabs.action.Action;
import msifeed.mc.misca.crabs.action.ActionManager;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.utils.AbstractMessage;

public class FighterMessage extends AbstractMessage<FighterMessage> {
    Type type;
    Stats stat;
    Action action;
    int mod;
    private String actionName;

    public FighterMessage() {
    }

    public FighterMessage(Type type) {
        this.type = type;
    }

    public FighterMessage(String actionName, int mod) {
        this.type = Type.ACTION;
        this.mod = mod;
        this.actionName = actionName;
    }

    public FighterMessage(Stats stat, int mod) {
        this.type = Type.CALC;
        this.mod = mod;
        this.stat = stat;
    }

    public FighterMessage(int mod) {
        this.type = Type.MOD;
        this.mod = mod;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readByte()];
        switch (type) {
            case ACTION:
                mod = buf.readInt();
                final String name = readShortString(buf);
                action = ActionManager.INSTANCE.lookup(name);
                break;
            case MOD:
                mod = buf.readInt();
                break;
            case CALC:
                mod = buf.readInt();
                final byte ord = buf.readByte();
                if (ord < Stats.values().length) stat = Stats.values()[ord];
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        switch (type) {
            case ACTION:
                buf.writeInt(mod);
                writeShortString(buf, actionName);
                break;
            case MOD:
                buf.writeInt(mod);
                break;
            case CALC:
                buf.writeInt(mod);
                buf.writeByte(stat.ordinal());
                break;
        }
    }

    @Override
    public FighterMessage onMessage(FighterMessage message, MessageContext ctx) {
        FightManager.INSTANCE.onMessageFromClient(ctx.getServerHandler().playerEntity, message);
        return null;
    }

    public enum Type {
        JOIN, ACTION, MOD, CALC, LEAVE
    }
}
