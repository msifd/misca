package msifeed.misca.charstate.cap;

import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charsheet.CharNeed;
import msifeed.sys.cap.FloatContainer;

public class CharstateImpl implements ICharstate {
    private long updateTime;
    private long miningTime;
    private int nonce;

    private final FloatContainer<CharEffort> efforts = new FloatContainer<>(CharEffort.class, 0, 0, 50);
    private final FloatContainer<CharNeed> tolerances = new FloatContainer<>(CharNeed.class, 0, 0, 1);

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
    public FloatContainer<CharNeed> tolerances() {
        return tolerances;
    }

    @Override
    public void replaceWith(ICharstate other) {
        updateTime = other.getUpdateTime();
        miningTime = other.getMiningTime();
        nonce = other.nonce();
        efforts.replaceWith(other.efforts());
        tolerances.replaceWith(other.tolerances());
    }
}
