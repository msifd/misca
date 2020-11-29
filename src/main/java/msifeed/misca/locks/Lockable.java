package msifeed.misca.locks;

public class Lockable implements ILockable {
    private boolean locked = false;
    private String secret = NO_SECRET;

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
