package msifeed.sys.sync;

import com.google.gson.reflect.TypeToken;
import msifeed.sys.rpc.RpcChannel;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class SyncChannel<T> extends JsonConfig<T> {
    private final RpcChannel rpc;
    private final String rpcSyncMethod;

    public SyncChannel(RpcChannel rpc, String fileName, TypeToken<T> type) {
        super(fileName, type);

        this.rpc = rpc;
        this.rpcSyncMethod = "sync." + type.getRawType().getSimpleName();

        try {
            final Method syncMethod = getClass().getDeclaredMethod("onSyncMessage", byte[].class);
            rpc.register(rpcSyncMethod, this, syncMethod);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onSyncMessage(byte[] jsonBytes) {
        updateValue(gson.fromJson(new String(jsonBytes, StandardCharsets.UTF_8), type.getType()));
    }

    public void sync() throws Exception {
        super.sync();
        broadcast();
    }

    public void broadcast() {
        final byte[] jsonBytes = gson.toJson(value).getBytes(StandardCharsets.UTF_8);
        rpc.sendToAll(rpcSyncMethod, (Object) jsonBytes);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player.world.isRemote) return;
        final byte[] jsonBytes = gson.toJson(value).getBytes(StandardCharsets.UTF_8);
        rpc.sendTo((EntityPlayerMP) event.player, rpcSyncMethod, (Object) jsonBytes);
    }

    protected void updateValue(T newValue) {
        super.updateValue(newValue);

        if (newValue != null)
            MinecraftForge.EVENT_BUS.post(new SyncEvent<>(newValue));
    }
}
