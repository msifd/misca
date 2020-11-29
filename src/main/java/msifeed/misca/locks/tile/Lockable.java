package msifeed.misca.locks.tile;

public class Lockable implements ILockable {
    private boolean locked = false;
    private String secret = NO_SECRET;

    public Lockable() {
    }

    public Lockable(boolean locked, String secret) {
        this.locked = locked;
        this.secret = secret;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean value) {
        this.locked = value;
    }

    @Override
    public String getSecret() {
        return secret;
    }

    @Override
    public void setSecret(String value) {
        this.secret = value;
    }
}
