package msifeed.sys.sync;

import msifeed.sys.rpc.RpcChannel;
import net.minecraft.entity.player.EntityPlayerMP;

import java.lang.reflect.Field;
import java.util.HashMap;

public class SyncChannel {
    private final RpcChannel rpc;
    private final String syncChannel;

    private HashMap<String, SyncField> syncFields = new HashMap<>();

    public SyncChannel(RpcChannel rpc, String syncChannel) {
        this.rpc = rpc;
        this.syncChannel = syncChannel;
    }

    public void register(Object obj) {
        final Class<?> type = obj.getClass();
        for (Field f : type.getDeclaredFields()) {
            final Sync ann = f.getAnnotation(Sync.class);
            if (ann != null)
                syncFields.put(ann.value(), new SyncField(obj, f, ann.value()));
        }
    }

    public void sync(EntityPlayerMP player) {

    }

    private static class SyncField {
        final Object object;
        final Field field;
        final String id;

        SyncField(Object o, Field f, String id) {
            this.object = o;
            this.field = f;
            this.id = id;
        }
    }
}
