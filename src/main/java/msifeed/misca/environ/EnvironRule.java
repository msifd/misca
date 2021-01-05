package msifeed.misca.environ;

import java.time.ZoneId;
import java.time.ZoneOffset;

public class EnvironRule {
    public Rain rain = null;
    public Time time = null;

    public static class Rain {
        public long income;
        public long outcome;
        public long thunder;
        public long min;
        public long max;
        public int dice;
    }

    public static class Time {
        public ZoneId timezone = ZoneOffset.UTC;
        public double scale = 0;
    }
}
