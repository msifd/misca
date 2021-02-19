package msifeed.misca.locks.cap.tile;

public class LockableImpl implements ILockable {
    private boolean locked = false;
    private int secret;

    public LockableImpl() {
    }

    public LockableImpl(boolean locked, int secret) {
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
    public int getSecret() {
        return secret;
    }

    @Override
    public void setSecret(int value) {
        this.secret = value;
    }
}
