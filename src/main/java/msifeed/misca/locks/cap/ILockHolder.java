package msifeed.misca.locks.cap;

import msifeed.misca.locks.cap.tile.ILockable;

public interface ILockHolder extends ILockable {
    boolean addLock(int secret);
    boolean removeLock();
}
