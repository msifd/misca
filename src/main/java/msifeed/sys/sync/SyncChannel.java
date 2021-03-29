package msifeed.sys.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import msifeed.sys.rpc.RpcChannel;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
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
import java.time.ZoneId;

public class SyncChannel<T> {
    private final TypeToken<T> type;
    private final RpcChannel rpc;
    private final String rpcSyncMethod;
    private final Path filePath;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(ZoneId.class, new ZoneIdAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    private T value;

    public SyncChannel(RpcChannel rpc, Path filePath, TypeToken<T> type) {
        this.type = type;
        this.rpc = rpc;
        this.rpcSyncMethod = "sync." + type.getRawType().getSimpleName();
        this.filePath = Loader.instance().getConfigDir().toPath().resolve(filePath);
        this.value = getDefaultInstance();

        try {
            final Method syncMethod = getClass().getDeclaredMethod("onSyncMessage", byte[].class);
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

    public void onSyncMessage(byte[] jsonBytes) {
        updateValue(gson.fromJson(new String(jsonBytes, StandardCharsets.UTF_8), type.getType()));
    }

    public void sync() throws Exception {
        if (!Files.exists(filePath)) {
            writeFile();
            return;
        }

        final String oldState = gson.toJson(value);
        final BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
        updateValue(gson.fromJson(reader, type.getType()));
        broadcast();

        final String newState = gson.toJson(value);
        if (!oldState.equals(newState)) {
            writeFile();
        }
    }

    private void writeFile() throws IOException {
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, gson.toJson(value).getBytes(StandardCharsets.UTF_8));
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

    private T getDefaultInstance() {
        try {
            return (T) type.getRawType().newInstance();
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
