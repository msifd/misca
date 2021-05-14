package msifeed.misca.charstate;

import com.google.gson.reflect.TypeToken;
import msifeed.misca.Misca;
import msifeed.misca.charstate.cap.*;
import msifeed.misca.charstate.client.CharstateHudHandler;
import msifeed.misca.charstate.handler.*;
import msifeed.misca.chatex.SpeechEvent;
import msifeed.misca.regions.RegionControl;
import msifeed.sys.sync.SyncChannel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum Charstate {
    INSTANCE;

    private static final int UPDATE_INTERVAL_SEC = 2;

    private final IntegrityHandler integrityHandler = new IntegrityHandler();
    private final SanityHandler sanityHandler = new SanityHandler();
    private final StaminaHandler staminaHandler = new StaminaHandler();
    private final CorruptionHandler corruptionHandler = new CorruptionHandler();
    private final EffortsHandler effortsHandler = new EffortsHandler();
    private final EffectsHandler effectsHandler = new EffectsHandler();

    public static class ItemEffectsConfig extends HashMap<ResourceLocation, ItemEffectInfo[]> {
    }

    public static final SyncChannel<ItemEffectsConfig> ITEM_EFFECTS
            = new SyncChannel<>(Misca.RPC, "item_effects.json", TypeToken.get(ItemEffectsConfig.class));

    public static HashMap<ResourceLocation, ItemEffectInfo[]> getItemEffects() {
        return ITEM_EFFECTS.get();
    }

    public void preInit() {
        CapabilityManager.INSTANCE.register(ICharstate.class, new CharstateStorage(), CharstateImpl::new);
        MinecraftForge.EVENT_BUS.register(new CharstateEventHandler());
        MinecraftForge.EVENT_BUS.register(this);
        Misca.RPC.register(new CharstateSync());

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new CharstateHudHandler());
        }
    }

    public static void sync() throws Exception {
        ITEM_EFFECTS.sync();
    }

    @SubscribeEvent
    public void onPlayerCreate(EntityEvent.EntityConstructing event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        final AbstractAttributeMap attributes = ((EntityLivingBase) event.getEntity()).getAttributeMap();
        attributes.registerAttribute(IntegrityHandler.INTEGRITY);
        attributes.registerAttribute(SanityHandler.SANITY);
        attributes.registerAttribute(StaminaHandler.STAMINA);
        attributes.registerAttribute(CorruptionHandler.CORRUPTION);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        final AbstractAttributeMap src = event.getOriginal().getAttributeMap();
        final AbstractAttributeMap dst = event.getEntityPlayer().getAttributeMap();
        copyAttribute(src, dst, IntegrityHandler.INTEGRITY);
        copyAttribute(src, dst, SanityHandler.SANITY);
        copyAttribute(src, dst, StaminaHandler.STAMINA);
        copyAttribute(src, dst, CorruptionHandler.CORRUPTION);
    }

    private static void copyAttribute(AbstractAttributeMap src, AbstractAttributeMap dst, IAttribute attr) {
        final double base = src.getAttributeInstance(attr).getBaseValue();
        dst.getAttributeInstance(attr).setBaseValue(base);
//        final ModifiableAttributeInstance instance = (ModifiableAttributeInstance) dst.getAttributeInstance(attr);
//        for (AttributeModifier mod : src.getAttributeInstance(attr).getModifiers()) {
//            instance.applyModifier(mod);
//        }
    }

    private static Map<CharNeed, Double> getEffects(EntityPlayer player) {
        return RegionControl.getLocalRules(player.world, player.getPositionVector())
                .flatMap(r -> r.needs.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingDouble(Map.Entry::getValue)));
    }

    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof EntityPlayerMP)) return;
        if (event.getEntity().world.isRemote) return;

        final EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();

        final ICharstate state = CharstateProvider.get(player);
        final long passedSec = state.passedFromUpdate();
        if (passedSec < UPDATE_INTERVAL_SEC) return;

        final Map<CharNeed, Double> effects = getEffects(player);
        integrityHandler.handleTime(player, passedSec, effects.getOrDefault(CharNeed.INT, 0d));
        sanityHandler.handleTime(player, UPDATE_INTERVAL_SEC, effects.getOrDefault(CharNeed.SAN, 0d));
        staminaHandler.handleTime(player, passedSec, effects.getOrDefault(CharNeed.STA, 0d));
        corruptionHandler.handleTime(player, passedSec);
        effortsHandler.handleTime(player, passedSec);

        state.resetUpdateTime();
        CharstateSync.sync(player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDamaged(LivingDamageEvent event) {
        if (event.isCanceled() || event.getAmount() < 1) return;
        if (event.getEntity().world.isRemote) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        final float amount = Math.min(player.getHealth(), event.getAmount());

        integrityHandler.handleDamage(player, amount);
        sanityHandler.handleDamage(player, amount);
    }

    @SubscribeEvent
    public void onSpeech(SpeechEvent event) {
        final double factorMod = getEffects(event.getPlayer()).getOrDefault(CharNeed.SAN, 0d);
        sanityHandler.handleSpeech(event.getPlayer(), event.getMessage(), factorMod);
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntityPlayer().getHeldItemMainhand().isEmpty()) return;

        staminaHandler.handleMining(event);
    }

    @SubscribeEvent
    public void onItemCrafted(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
        if (event.player.world.isRemote) return;

        staminaHandler.handleCrafting(event);
    }

    public void onFoodEaten(EntityPlayer player, ItemStack stack) {
        if (player.world.isRemote) return;

        sanityHandler.handleItemUse(player, stack);
        effectsHandler.handleItemUse(player, stack);
    }
}
