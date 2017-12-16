package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.utils.AbstractMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class FighterContextMessage extends AbstractMessage<FighterContextMessage> {
    private ArrayList<FighterContext> vctx = new ArrayList<>();

    public FighterContextMessage() {

    }

    public FighterContextMessage(Collection<FighterContext> vctx) {
        this.vctx.addAll(vctx);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        ByteBuf payload = buf.readBytes(size);

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(payload.array());
            ObjectInputStream ois = new ObjectInputStream(bis);
            this.vctx = (ArrayList<FighterContext>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this.vctx);

            byte[] bytes = bos.toByteArray();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FighterContextMessage onMessage(FighterContextMessage message, MessageContext ctx) {
        BattleManager.INSTANCE.onUpdateFromServer(message.vctx);
        return null;
    }
}
