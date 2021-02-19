package msifeed.misca.locks.cap.key;

public interface ILockKey {
    int getSecret();

    void setSecret(int value);
}
