package msifeed.misca.needs.cap;

import java.util.EnumMap;

public class PlayerNeeds implements IPlayerNeeds {
    private final EnumMap<NeedType, Double> needs = new EnumMap<>(NeedType.class);
    private long updateTime;
    private long miningTime;

    @Override
    public double get(NeedType needType) {
        return needs.getOrDefault(needType, needType.def);
    }

    @Override
    public void set(NeedType needType, double value) {
        needs.put(needType, needType.clamp(value));
    }

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
    public void replaceWith(IPlayerNeeds other) {
        for (NeedType nt : NeedType.values())
            this.needs.put(nt, other.get(nt));
        this.setUpdateTime(other.getUpdateTime());
        this.setMiningTime(other.getMiningTime());
    }
}
