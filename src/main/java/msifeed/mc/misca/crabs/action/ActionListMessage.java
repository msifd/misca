package msifeed.mc.misca.crabs.action;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.utils.AbstractMessage;

import java.util.ArrayList;
import java.util.Collection;

public class ActionListMessage extends AbstractMessage<ActionListMessage> {
    private ArrayList<String> actions = new ArrayList<>();

    public ActionListMessage() {

    }

    public ActionListMessage(Collection<Action> actions) {
        for (Action a : actions)
            this.actions.add(a.signature());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final int cnt = buf.readInt();
        actions = new ArrayList<>(cnt);
        for (int i = 0; i < cnt; i++) {
            actions.add(readShortString(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(actions.size());
        for (String s : actions) {
            writeShortString(buf, s);
        }
    }

    @Override
    public IMessage onMessage(ActionListMessage message, MessageContext ctx) {
        ActionManager.INSTANCE.onReceiveActionSignatures(message.actions);
        return null;
    }
}
