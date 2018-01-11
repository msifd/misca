package msifeed.mc.misca.books;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.utils.AbstractMessage;

public class MessageRemoteBook extends AbstractMessage<MessageRemoteBook> {
    private Type type;
    private String text;
    private RemoteBook.Style style = RemoteBook.Style.BOOK;

    public MessageRemoteBook() {
        this.type = Type.CHECK;
    }

    public MessageRemoteBook(boolean check) {
        this.type = Type.CHECK;
        this.text = check ? "y" : "n";
    }

    public MessageRemoteBook(String text, boolean isCheck) {
        this.type = isCheck ? Type.CHECK : Type.REQUEST_RESPONSE;
        this.text = text;
    }

    public MessageRemoteBook(RemoteBook.Style style, String name) {
        this.type = Type.SIGN;
        this.style = style;
        this.text = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readByte()];
        text = readString(buf);
        if (type == Type.SIGN) {
            style = RemoteBook.Style.valueOf(readShortString(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        writeString(buf, text);
        if (type == Type.SIGN) {
            writeShortString(buf, style.toString());
        }
    }

    @Override
    public IMessage onMessage(MessageRemoteBook message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            switch (message.type) {
                case CHECK:
                    return new MessageRemoteBook(RemoteBookManager.INSTANCE.checkBook(message.text));
                case SIGN:
                    RemoteBookManager.INSTANCE.signBook(ctx.getServerHandler().playerEntity, message.style, message.text);
                    return null;
                case REQUEST_RESPONSE:
                    return new MessageRemoteBook(RemoteBookManager.INSTANCE.loadBook(message.text), false);
            }
        } else {
            switch (message.type) {
                case CHECK:
                    RemoteBookManager.INSTANCE.receiveCheck(message.text.equals("y"));
                    break;
                case REQUEST_RESPONSE:
                    RemoteBookManager.INSTANCE.receiveResponse(message.text);
                    break;
            }
        }
        return null;
    }

    private enum Type {
        CHECK, REQUEST_RESPONSE, SIGN
    }
}
