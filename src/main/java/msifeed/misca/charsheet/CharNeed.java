package msifeed.misca.charsheet;

import msifeed.misca.charsheet.cap.CharsheetProvider;
import net.minecraft.entity.player.EntityPlayer;

public enum CharNeed {
    COR, INT, SAN, STA;

    public double restFactor(EntityPlayer player) {
        return CharsheetProvider.get(player).needsRest().get(this);
    }

    public double lostFactor(EntityPlayer player) {
        return CharsheetProvider.get(player).needsLost().get(this);
    }
}
