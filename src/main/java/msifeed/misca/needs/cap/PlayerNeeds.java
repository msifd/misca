package msifeed.misca.needs.cap;

public class PlayerNeeds implements IPlayerNeeds {
    private long updateTime;
    private long miningTime;

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
}
