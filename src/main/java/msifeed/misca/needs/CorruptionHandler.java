package msifeed.misca.needs;

import msifeed.misca.Misca;
import msifeed.misca.needs.cap.IPlayerNeeds;

public class CorruptionHandler {
    public void handleTime(IPlayerNeeds needs, long secs) {
        final NeedsConfig config = Misca.getSharedConfig().needs;
        final double restored = secs * config.corruptionRestPerSec;
        needs.add(IPlayerNeeds.NeedType.corruption, restored);
    }
}
