package msifeed.mc.misca.crabs.context;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import msifeed.mc.misca.crabs.CrabsNetwork;
import msifeed.mc.misca.crabs.fight.MoveManager;
import msifeed.mc.misca.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public enum ContextManager {
    INSTANCE;

    private Logger logger = LogManager.getLogger("Crabs.Context");
    private HashMap<UUID, Context> uuidToContext = new HashMap<>();

    private long lastUpdate = 0;
    private Set<Context> toSync = new HashSet<>();

    public void onInit() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

//    public Collection<Context> getContexts() {
//        return uuidToContext.values();
//    }

    public Context getContext(UUID uuid) {
        return uuidToContext.get(uuid);
    }

    public Context getContext(EntityLivingBase entity) {
        return uuidToContext.get(EntityUtils.getUuid(entity));
    }

    /**
     * Возвращает контекст энтити или регистрирует для него новый, если ничего не нашлось
     */
    public Context getOrCreateContext(EntityLivingBase entity) {
        return uuidToContext.computeIfAbsent(
                EntityUtils.getUuid(entity),
                uuid -> {
                    final Context context = new Context(uuid, entity);
                    context.updateStatus(Context.Status.NEUTRAL);
                    toSync.add(context);
                    return context;
                });
    }

    public void syncContext(Context ctx) {
        toSync.add(ctx);
    }

    /**
     * Тут клиенты получают контексты от сервера и удаляют контексты мертвых энтити.
     */
    public void receiveContexts(Collection<Context> contexts) {
        final int currentDim = Minecraft.getMinecraft().thePlayer.dimension;
        uuidToContext.values().removeIf(context -> context.entity != null && context.entity.dimension != currentDim);

        for (Context c : contexts) {
            if (c.status == Context.Status.DELETE)
                uuidToContext.remove(c.uuid);
            else
                uuidToContext.put(c.uuid, c);
        }
    }

    /**
     * Когда игрок попадает в мир, он:
     * получает новый контекст, а если он уже есть то сбрасывает его и обновляет энтити,
     * получает локальные для измерения контексты, потому что он может искать сущности только в пределах своего измерения.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onJoinWorld(EntityJoinWorldEvent event) {
        // TODO решить что делать с энтитями
        if (!(event.entity instanceof EntityPlayerMP)) return;
        final EntityPlayerMP player = (EntityPlayerMP) event.entity;

        Context context = getContext(player.getUniqueID());
        if (context == null) {
            context = new Context(player.getUniqueID(), player);
            uuidToContext.put(context.uuid, context);
        } else {
            resetContext(context);
            context.entity = player;
        }

        final ContextMessage msg = new ContextMessage(getLocalContexts(event.entity.dimension));
        CrabsNetwork.INSTANCE.sendToPlayer(player, msg);

        toSync.add(context);
    }

    /**
     * Синхронизируем всех раз в секунду
     */
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        final int UPDATE_DELTA_MS = 1000;
        final long now = System.currentTimeMillis();
        if (now - lastUpdate < UPDATE_DELTA_MS) return;
        lastUpdate = now;

        MoveManager.INSTANCE.finalizeMoves();

        if (toSync.isEmpty()) return;
        CrabsNetwork.INSTANCE.sendToAll(new ContextMessage(toSync));
        toSync.clear();
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        final Context context = uuidToContext.get(event.player.getUniqueID());
        if (context == null) return;
        context.reset();
        toSync.add(context);
    }

    /**
     * Игроки сбрасываются, энтити удаляются из списков, т.к. они одноразовые
     */
    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        final Context context = getContext(event.entityLiving);
        if (context == null) return;
        if (event.entity instanceof EntityPlayer) {
            context.reset(Context.Status.NEUTRAL);
        } else {
            context.reset(Context.Status.DELETE);
            uuidToContext.remove(context.uuid);
        }
        toSync.add(context);
    }

    /**
     * Отсоединяем направленные на контекст таргеты
     */
    public void unbindTargetToContext(Context context) {
        for (Context ctx : uuidToContext.values()) {
            if ((ctx.target != null && ctx.target.equals(context.uuid))
                    || (ctx.puppet != null && ctx.puppet.equals(context.uuid))) {
                ctx.reset();
                toSync.add(ctx);
            }
        }
    }

    public void resetContext(Context context) {
        unbindTargetToContext(context);
        context.reset();
        context.knockedOut = false; // Нужно ли тут сбрасывать не-лечением?
        toSync.add(context);
        logger.info("{} has been reseted", context.entity.getCommandSenderName());
    }

    private Collection<Context> getLocalContexts(int dimensionId) {
        return uuidToContext.values().stream()
                .filter(context -> context.entity != null && context.entity.dimension == dimensionId)
                .collect(Collectors.toList());
    }
}
