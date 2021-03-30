package msifeed.misca.client;

import msifeed.mellow.utils.Point;
import msifeed.misca.Misca;
import net.minecraftforge.common.config.Config;

@Config(modid = Misca.MODID)
public class MiscaConfig {
    public static String windowTitle = "Ariadna";
    public static boolean combatDebug = false;

    public static Point chatSize = new Point(320, 180);
}
