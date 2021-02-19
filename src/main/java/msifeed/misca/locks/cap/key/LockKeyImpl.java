package msifeed.misca.locks.cap.key;

public class LockKeyImpl implements ILockKey {
    private int secret = 0;

    @Override
    public int getSecret() {
        return secret;
    }

    @Override
    public void setSecret(int value) {
        this.secret = value;
    }
}
