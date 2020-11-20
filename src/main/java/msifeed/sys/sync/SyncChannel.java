package msifeed.sys.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import msifeed.sys.rpc.RpcChannel;
import msifeed.sys.rpc.RpcContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SyncChannel<T> {
    private final Class<T> type;
    private final RpcChannel rpc;
    private final String rpcSyncMethod;
    private final Path filePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private T value;

    public SyncChannel(RpcChannel rpc, Path filePath, Class<T> type) {
        this.type = type;
        this.rpc = rpc;
        this.rpcSyncMethod = "sync." + type.getSimpleName();
        this.filePath = Loader.instance().getConfigDir().toPath().resolve(filePath);
        this.value = getDefaultInstance();

        try {
            final Method syncMethod = getClass().getDeclaredMethod("onSyncMessage", RpcContext.class, String.class);
            rpc.register(rpcSyncMethod, this, syncMethod);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Nonnull
    public T get() {
        return value;
    }

    public void onSyncMessage(RpcContext ctx, String json) {
        updateValue(gson.fromJson(json, type));
    }

    public void load() throws Exception {
        if (!Files.isRegularFile(filePath)) {
            writeFile();
            return;
        }

        final BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
        updateValue(gson.fromJson(reader, type));
        broadcast();
    }

    private void writeFile() throws IOException {
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, gson.toJson(value).getBytes(StandardCharsets.UTF_8));
    }

    public void broadcast() {
        rpc.sendToAll(rpcSyncMethod, gson.toJson(value));
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player.world.isRemote) return;
        rpc.sendTo((EntityPlayerMP) event.player, rpcSyncMethod, gson.toJson(value));
    }

    private T getDefaultInstance() {
        try {
            return type.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void updateValue(T newValue) {
        if (newValue == null) return;
        value = newValue;
        MinecraftForge.EVENT_BUS.post(new SyncEvent<>(newValue));
    }
}
