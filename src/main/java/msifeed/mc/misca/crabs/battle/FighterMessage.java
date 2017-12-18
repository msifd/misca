package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.crabs.actions.Action;
import msifeed.mc.misca.crabs.actions.ActionManager;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.utils.AbstractMessage;

public class FighterMessage extends AbstractMessage<FighterMessage> {
    Type type;
    Stats stat;
    Action action;
    int mod;
    private String actionName;

    private boolean malformed = false;

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

    public FighterMessage(Stats stat) {
        this.type = Type.CALC;
        this.stat = stat;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readByte()];
        switch (type) {
            case ACTION:
                final String name = readShortString(buf);
                action = ActionManager.INSTANCE.lookup(name);
                mod = buf.readInt();
                if (action == null) malformed = true;
                break;
            case CALC:
                final byte ord = buf.readByte();
                if (ord < Stats.values().length) stat = Stats.values()[ord];
                else malformed = true;
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        switch (type) {
            case ACTION:
                writeShortString(buf, actionName);
                buf.writeInt(mod);
                break;
            case CALC:
                buf.writeByte(stat.ordinal());
                break;
        }
    }

    @Override
    public FighterMessage onMessage(FighterMessage message, MessageContext ctx) {
        if (!message.malformed)
            BattleManager.INSTANCE.onMessageFromClient(ctx.getServerHandler().playerEntity, message);
        return null;
    }

    public enum Type {
        JOIN, ACTION, CALC, LEAVE
    }
}
