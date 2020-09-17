package msifeed.sys.rpc;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

public class RpcException extends RuntimeException {
    private final ICommandSender sender;

    public RpcException(ICommandSender sender, String msg) {
        super(msg);
        this.sender = sender;
    }

    public void send() {
        sender.sendMessage(new TextComponentString("Error: " + getMessage()));
    }
}
