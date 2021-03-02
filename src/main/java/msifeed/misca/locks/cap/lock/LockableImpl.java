package msifeed.misca.locks.cap.lock;

import msifeed.misca.locks.LockType;

public class LockableImpl implements ILockable {
    private LockType type = LockType.mechanical;
    private boolean locked = false;
    private int secret;

    public LockableImpl() {
    }

    public LockableImpl(LockType type, boolean locked, int secret) {
        this.type = type;
        this.locked = locked;
        this.secret = secret;
    }

    @Override
    public LockType getType() {
        return type;
    }

    @Override
    public void setType(LockType type) {
        this.type = type;
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
