package msifeed.mc.aorta.locks;

import java.util.stream.Stream;

public enum LockType {
    NONE, BUILD_IN, PADLOCK, DIGITAL;

    public static Stream<LockType> locks() {
        return Stream.of(LockType.values()).skip(1);
    }
}
