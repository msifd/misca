package msifeed.misca.charstate.cap;

import msifeed.misca.charsheet.CharEffort;
import msifeed.sys.cap.FloatContainer;

public class CharstateImpl implements ICharstate {
    private long updateTime;
    private long miningTime;
    private final FloatContainer<CharEffort> efforts = new FloatContainer<>(CharEffort.class, 0, 0, 50);
    private int nonce;

    @Override
    public long getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(long value) {
        this.updateTime = value;
    }

    @Override
    public long getMiningTime() {
        return miningTime;
    }

    @Override
    public void setMiningTime(long value) {
        this.miningTime = value;
    }

    @Override
    public FloatContainer<CharEffort> efforts() {
        return efforts;
    }

    @Override
    public int nonce() {
        return nonce;
    }

    @Override
    public void incNonce() {
        nonce++;
    }

    @Override
    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    @Override
    public void replaceWith(ICharstate other) {
        updateTime = other.getUpdateTime();
        miningTime = other.getMiningTime();
        efforts.replaceWith(other.efforts());
        nonce = other.nonce();
    }
}
