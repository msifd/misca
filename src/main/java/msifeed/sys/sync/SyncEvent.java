package msifeed.sys.sync;

import net.minecraftforge.fml.common.eventhandler.Event;

public class SyncEvent<T> extends Event {
    public final T value;

    public SyncEvent(T value) {
        this.value = value;
    }

    //    public static class Load<T> extends SyncEvent<T> {
//        public Load(T value) {
//            super(value);
//        }
//    }
//
//    public static class Sync<T> extends SyncEvent<T> {
//        public Sync(T value) {
//            super(value);
//        }
//    }
}
