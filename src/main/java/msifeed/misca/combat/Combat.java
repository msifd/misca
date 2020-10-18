package msifeed.misca.combat;

import net.minecraftforge.common.MinecraftForge;

public class Combat {
    public void init() {
        MinecraftForge.EVENT_BUS.register(new DamageHandler());
    }
}
