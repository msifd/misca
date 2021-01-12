package msifeed.misca.needs.cap;

public class AbsoluteTime implements IAbsoluteTime {
    private long time = 0;

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void setTime(long value) {
        this.time = value;
    }
}
