package msifeed.sys.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import msifeed.misca.Misca;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;

public class JsonConfig<T> {
    protected final TypeToken<T> type;
    protected final Path filePath;
    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(ZoneId.class, new ZoneIdAdapter())
            .registerTypeAdapter(AxisAlignedBB.class, new AABBAdapter())
            .registerTypeAdapter(Class.class, new ClassAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    protected T value;

    public JsonConfig(String fileName, TypeToken<T> type) {
        this.type = type;
        this.filePath = Loader.instance().getConfigDir().toPath().resolve(Misca.MODID).resolve(fileName);
        this.value = getDefaultInstance();
    }

    @Nonnull
    public T get() {
        return value;
    }

    public void sync() throws Exception {
        if (!Files.exists(filePath)) {
            writeFile();
            return;
        }

        final String oldState = gson.toJson(value);
        final BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
        updateValue(gson.fromJson(reader, type.getType()));

        final String newState = gson.toJson(value);
        if (!oldState.equals(newState)) {
            writeFile();
        }
    }

    protected void updateValue(T newValue) {
        if (newValue == null) return;
        value = newValue;
    }

    public void writeFile() throws IOException {
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, gson.toJson(value).getBytes(StandardCharsets.UTF_8));
    }

    private T getDefaultInstance() {
        try {
            return (T) type.getRawType().newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
