package msifeed.misca.environ;

import msifeed.misca.Misca;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Environ {
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Nullable
    private static EnvironRule getRule(World world) {
        return Misca.getSharedConfig().environ.get(world.provider.getDimension());
    }

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        final EnvironRule rule = getRule(event.world);
        if (rule == null) return;

        final EnvironWorldData data = EnvironWorldData.get(event.world);
        handleRain(event.world, rule, data);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        final World world = Minecraft.getMinecraft().world;
        if (world == null)
            return;

        handleTime(world, getRule(world));
    }

    private void handleTime(World world, EnvironRule rule) {
        if (rule == null || rule.time == null) return;

        if (rule.time.timezone != null)
            setRealTime(world, rule.time.timezone);
        else if (rule.time.scale > 0)
            setScaledTime(world, rule.time.scale);
    }

    private void setRealTime(World world, ZoneId timezone) {
        final Duration mcTimeFix = Duration.ofHours(6);
        final LocalDateTime now = LocalDateTime.now(timezone).minus(mcTimeFix);

        final long secs = now.toLocalTime().toSecondOfDay();

        final long day = now.toLocalDate().toEpochDay();
        final long moonPhaseTime = (day % 8) * 24000;

        final long time = (secs * 23999) / 86400;
        world.setWorldTime(time + moonPhaseTime);
    }

    private void setScaledTime(World world, double scale) {
        final WorldInfo wi = world.getWorldInfo();
        final long tt = wi.getWorldTotalTime();
        wi.setWorldTime((long) (tt * scale));
    }

    private void handleRain(World world, EnvironRule rule, EnvironWorldData data) {
        if (rule.rain == null) return;

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
            final boolean shouldRoll = data.rainAcc >= r.min && r.dice > 0;
            final boolean roll = shouldRoll && world.rand.nextInt(r.dice) == 0;
            if (roll) {
                System.out.printf("Environ: [%s] successful rainfall dice roll (while %d>=%d)%n",
                        wi.getWorldName(), data.rainAcc, r.min);
            }
            if (roll || data.rainAcc > r.max) {
                wi.setRaining(true);
                wi.setThundering(data.rainAcc > r.thunder);
            }
        }
    }
}
