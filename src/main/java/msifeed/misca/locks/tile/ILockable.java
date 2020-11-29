package msifeed.misca.locks.tile;

public interface ILockable {
    String NO_SECRET = "";

    boolean isLocked();

    void setLocked(boolean value);

    default boolean hasSecret() {
        return !getSecret().equals(NO_SECRET);
    }

    String getSecret();

    void setSecret(String value);

    default boolean canOpenWith(String key) {
        return getSecret().equals(key);
    }
}
