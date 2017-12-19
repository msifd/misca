package msifeed.mc.misca.crabs.battle;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import msifeed.mc.misca.crabs.CrabsNetwork;
import msifeed.mc.misca.crabs.rules.Rules;
import msifeed.mc.misca.utils.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public enum BattleManager {
    INSTANCE;

    private Logger logger = LogManager.getLogger("Crabs.Battle");
    private HashMap<UUID, FighterContext> uuidToContext = new HashMap<>();

    private long lastUpdate = 0;
    private Set<FighterContext> toSync = new HashSet<>();

    public void onInit() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    public boolean isBattling(EntityLivingBase entity) {
        return uuidToContext.containsKey(EntityUtils.getUuid(entity));
    }

    public FighterContext getContext(UUID uuid) {
        return uuidToContext.get(uuid);
    }

    public FighterContext getContext(EntityLivingBase entity) {
        return uuidToContext.get(EntityUtils.getUuid(entity));
    }

    public Collection<FighterContext> getContexts() {
        return uuidToContext.values();
    }

    protected void syncContext(FighterContext ctx) {
        toSync.add(ctx);
    }

    public void joinBattle(EntityLivingBase entity) {
        if (isBattling(entity)) return;

        final FighterContext ctx = new FighterContext(entity);
        uuidToContext.put(ctx.uuid, ctx);
        toSync.add(ctx);

        logger.info("{} is joined the battle.", ctx.entity.getCommandSenderName());
    }

    public void toggleControl(EntityPlayer player, EntityLivingBase entity) {
        // Игроков нельзя контролить, да?
        if (entity instanceof EntityPlayer) return;

        final FighterContext controller = uuidToContext.get(player.getUniqueID());
        final FighterContext actor = getContext(entity);
        if (controller == null || actor == null) return;

        // Выключаем контроль при повторном вызове или устанавливаем новую марионетку
        controller.control = (controller.control == actor.uuid) ? null : actor.uuid;

        if (player instanceof EntityPlayerMP) {
            final FighterContextMessage msg = new FighterContextMessage(ImmutableList.of(controller, actor));
            CrabsNetwork.INSTANCE.sendToPlayer((EntityPlayerMP) player, msg);
        }
    }

    public void leaveBattle(EntityLivingBase entity, boolean instant) {
        final FighterContext ctx = uuidToContext.get(EntityUtils.getUuid(entity));
        if (ctx == null) return;

        // Энтити выходят из боя мгновенно
        if (instant || !(ctx.entity instanceof EntityPlayer)) {
            removeFromBattle(ctx);
            return;
        }

        if (ctx.status == FighterContext.Status.LEAVING) {
            // Отмена выхода из боя
            ctx.updateStatus(FighterContext.Status.ACT);
            logger.info("{} is not leaving battle anymore.", ctx.entity.getCommandSenderName());
        } else {
            ctx.updateStatus(FighterContext.Status.LEAVING);
            logger.info("{} is leaving battle.", ctx.entity.getCommandSenderName());
        }
        toSync.add(ctx);
    }

    public void resetFighter(EntityLivingBase entity) {
        final FighterContext ctx = BattleManager.INSTANCE.getContext(entity);
        if (ctx == null) return;

        unbindFighter(ctx.uuid);
        ctx.reset(true);
        toSync.add(ctx);
        logger.info("{} has been reseted.", ctx.entity.getCommandSenderName());
    }

    private void unbindFighter(UUID fighter) {
        for (FighterContext ctx : uuidToContext.values()) {
            if (ctx.control == fighter) {
                ctx.control = null;
                toSync.add(ctx);
            }
            if (ctx.target == fighter) {
                ctx.target = null;
                toSync.add(ctx);
            }
        }
    }

    private void removeFromBattle(FighterContext ctx) {
        ctx.updateStatus(FighterContext.Status.REMOVED);
        uuidToContext.remove(ctx.uuid);

        unbindFighter(ctx.uuid);

        toSync.add(ctx);
        logger.info("{} removed from the battle.", ctx.entity.getCommandSenderName());
    }

    private void syncBattleList() {
        if (toSync.isEmpty()) return;
        CrabsNetwork.INSTANCE.sendToAll(new FighterContextMessage(toSync));
        toSync.clear();
    }

    void onMessageFromClient(EntityPlayerMP player, FighterMessage message) {
        FighterContext ctx = uuidToContext.get(player.getUniqueID());

        if (ctx == null) {
            if (message.type == FighterMessage.Type.JOIN) joinBattle(player);
            return;
        }

        switch (message.type) {
            case ACTION:
                FighterContext actor = ctx.control == null ? ctx : getContext(ctx.control);
                MoveManager.INSTANCE.selectAction(actor, message.action, message.mod);
                break;
            case CALC:
                Rules.rollSingleStat(player, message.stat, message.mod);
                break;
            case LEAVE:
                leaveBattle(player, false);
                break;
        }
    }

    void onUpdateFromServer(ArrayList<FighterContext> vctx) {
        for (FighterContext ctx : vctx) {
            if (ctx.status == FighterContext.Status.REMOVED) {
                uuidToContext.remove(ctx.uuid);
                continue;
            }

            final FighterContext oldCtx = uuidToContext.get(ctx.uuid);
            if (oldCtx != null && oldCtx.entity != null) ctx.entity = oldCtx.entity;
            else ctx.entity = EntityUtils.lookupPlayer(ctx.uuid);

            if (ctx.entity == null) throw new RuntimeException("Missing entity: " + ctx.uuid);

            uuidToContext.put(ctx.uuid, ctx);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        final int UPDATE_DELTA_MS = 500;
        final long now = System.currentTimeMillis();
        if (now - lastUpdate < UPDATE_DELTA_MS) return;
        lastUpdate = now;

        MoveManager.INSTANCE.finalizeMoves();
        syncBattleList();
    }

    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        final FighterContext ctx = getContext(event.entityLiving);
        if (ctx == null) return;

        final long now = System.currentTimeMillis() / 1000;
        final long statusAge = now - ctx.lastStateChange;

        switch (ctx.status) {
            case LEAVING:
                if (statusAge >= BattleDefines.SECS_BEFORE_LEAVE_BATTLE) removeFromBattle(ctx);
                break;
            case DEAL_DAMAGE:
                if (statusAge >= BattleDefines.SECS_TO_DEAL_DAMAGE) MoveManager.INSTANCE.stopDealingDamage(ctx);
                break;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamage(LivingAttackEvent event) {
        if (!(event.source instanceof EntityDamageSource)) return;

        final EntityDamageSource damage = (EntityDamageSource) event.source;
        final FighterContext attacker_ctx = getContext((EntityLivingBase) damage.getEntity());
        final FighterContext target_ctx = getContext(event.entityLiving);

        // Урон проходит как обычно только когда никто не дерется
        if (attacker_ctx == null && target_ctx == null) return;

        // Пришел урон по результатам хода
        if (damage instanceof CrabsDamage) return;

        // Бить бойцов покидающих бой можно!
        if (target_ctx != null && target_ctx.status == FighterContext.Status.LEAVING) return;

        // Отменяем получение урона. Его мы вернем после хода... может быть
        event.setCanceled(true);

        // Если в бою только одна сторона, то урон не возвращаем
        if (attacker_ctx == null || target_ctx == null) return;

        // Хотя бить может и закулисье, правила выдачи урона считаются для марионетки
        final FighterContext actor = attacker_ctx.control == null ? attacker_ctx : getContext(attacker_ctx.control);

        // Если атакующий еще не должен бить (завершать ход), то игнорим
        if (!actor.canAttack()) return;

        // После того как цель определена можно атаковать только её
        if (actor.target != null && actor.target != target_ctx.uuid) return;

        MoveManager.INSTANCE.dealDamage(actor, target_ctx, damage, event.ammount);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        final FighterContext context = uuidToContext.get(event.player.getUniqueID());
        if (context == null) return;

        context.entity = event.player;

        if (event.player instanceof EntityPlayerMP) {
            final FighterContextMessage msg = new FighterContextMessage(uuidToContext.values());
            CrabsNetwork.INSTANCE.sendToPlayer((EntityPlayerMP) event.player, msg);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        final FighterContext ctx = uuidToContext.get(event.player.getUniqueID());
        if (ctx == null) return;
        removeFromBattle(ctx);
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        final FighterContext ctx = getContext(event.entityLiving);
        if (ctx == null) return;
        removeFromBattle(ctx);
    }

    @SubscribeEvent
    public void onChatMessage(ServerChatEvent event) {
        final FighterContext ctx = uuidToContext.get(event.player.getUniqueID());
        if (ctx == null) return;

//        // Удаляем все что не буква, цифра или пробел
//        final String cleanedMsg = event.message.replaceAll("[^\\w\\d\\s]", "");
        final int words = event.message.split("\\s+").length;
        if (words < BattleDefines.MIN_WORDS_IN_ACTION_DESC) return;

        final FighterContext actor = ctx.control == null ? ctx : getContext(ctx.control);
        MoveManager.INSTANCE.describeAction(actor);
    }
}
