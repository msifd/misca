package msifeed.mc.misca.crabs.fight;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.crabs.action.Action;
import msifeed.mc.misca.crabs.action.ActionManager;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.rules.FistFight;
import msifeed.mc.misca.utils.AbstractMessage;

public class FighterMessage extends AbstractMessage<FighterMessage> {
    Type type;
    Stats stat;
    Action action;
    FistFight.Action fistAction;
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

    public FighterMessage(FistFight.Action action, int mod) {
        this.type = Type.FIST;
        this.mod = mod;
        this.fistAction = action;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readByte()];
        mod = buf.readInt();
        switch (type) {
            case ACTION:
                final String name = readShortString(buf);
                action = ActionManager.INSTANCE.lookup(name);
                break;
            case CALC: {
                final byte ord = buf.readByte();
                if (ord < Stats.values().length) stat = Stats.values()[ord];
                break;
            }
            case FIST: {
                final byte ord = buf.readByte();
                if (ord < FistFight.Action.values().length) fistAction = FistFight.Action.values()[ord];
                break;
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        buf.writeInt(mod);
        switch (type) {
            case ACTION:
                writeShortString(buf, actionName);
                break;
            case CALC:
                buf.writeByte(stat.ordinal());
                break;
            case FIST:
                buf.writeByte(fistAction.ordinal());
                break;
        }
    }

    @Override
    public FighterMessage onMessage(FighterMessage message, MessageContext ctx) {
        FightManager.INSTANCE.onMessageFromClient(ctx.getServerHandler().playerEntity, message);
        return null;
    }

    public enum Type {
        JOIN, ACTION, MOD, CALC, FIST, LEAVE, RESET
    }
}
