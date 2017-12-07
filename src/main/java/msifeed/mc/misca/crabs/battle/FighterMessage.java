package msifeed.mc.misca.crabs.battle;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.crabs.actions.Action;
import msifeed.mc.misca.crabs.actions.Actions;
import msifeed.mc.misca.crabs.character.Stats;

public class FighterMessage implements IMessage, IMessageHandler<FighterMessage, IMessage> {
    Type type;
    Action action;
    Stats stat;

    private boolean malformed = false;

    public FighterMessage() {
    }

    public FighterMessage(Type type) {
        this.type = type;
    }

    public FighterMessage(Action action) {
        this.type = Type.ACTION;
        this.action = action;
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
                byte len = buf.readByte();
                String name = new String(buf.readBytes(len).array(), Charsets.UTF_8);
                action = Actions.lookup(name);
                if (action == null) malformed = true;
                break;
            case CALC:
                byte ord = buf.readByte();
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
                String name = action.name;
                buf.writeByte(name.length());
                buf.writeBytes(name.getBytes(Charsets.UTF_8));
                break;
            case CALC:
                buf.writeByte(stat.ordinal());
                break;
        }
    }

    @Override
    public IMessage onMessage(FighterMessage message, MessageContext ctx) {
        if (message.malformed) return null;
        BattleManager.INSTANCE.onMessageFromClient(ctx.getServerHandler().playerEntity, message);
        return null;
    }

    public enum Type {
        JOIN, ACTION, CALC, LEAVE
    }
}
