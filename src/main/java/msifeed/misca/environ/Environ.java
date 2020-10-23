package msifeed.misca.environ;

import msifeed.misca.MiscaConfig;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Environ {
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        final EnvironRule rule = MiscaConfig.environ.get(event.world.getWorldInfo().getWorldName());
        if (rule == null) return;

        final EnvironWorldData data = EnvironWorldData.get(event.world);
        handleRain(event.world, rule, data);
    }

    private void handleRain(World world, EnvironRule rule, EnvironWorldData data) {
        final WorldInfo wi = world.getWorldInfo();
        final EnvironRule.Rain r = rule.rain;

        wi.setRainTime(Integer.MAX_VALUE);
        wi.setThunderTime(Integer.MAX_VALUE);

        if (wi.isRaining()) {
            data.rainAcc -= r.outcome;
            if (data.rainAcc <= 0) {
                wi.setRaining(false);
                wi.setThundering(false);
            }
        } else {
            data.rainAcc += r.income;
            final boolean roll = (data.rainAcc >= r.min) && world.rand.nextInt(r.dice) == 0;
            if (roll) {
                System.out.println(String.format("Environ: [%s] successful rainfall dice roll (while %d>=%d)",
                        wi.getWorldName(), data.rainAcc, r.min));
            }
            if (roll || data.rainAcc > r.max) {
                wi.setRaining(true);
                wi.setThundering(data.rainAcc > r.thunder);
            }
        }
    }
}
