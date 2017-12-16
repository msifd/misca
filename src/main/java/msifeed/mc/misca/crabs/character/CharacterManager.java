package msifeed.mc.misca.crabs.character;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.misca.config.ConfigEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.crabs.CrabsNetwork;
import msifeed.mc.misca.utils.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public enum CharacterManager {
    INSTANCE;

    private static final Character GENERIC_CHAR = new Character(new int[]{8, 8, 8, 8, 8, 8});
    private HashMap<UUID, Character> uuidToChar = new HashMap<>();

    @SideOnly(Side.CLIENT)
    private HashMap<UUID, Consumer<Character>> requests = new HashMap<>();

    public void onInit() {
        ConfigManager.INSTANCE.eventbus.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        uuidToChar.putAll(CharacterProvider.INSTANCE.load());
    }

    public Character get(UUID uuid) {
        return uuidToChar.getOrDefault(uuid, GENERIC_CHAR);
    }

    public void onServerReceive(CharacterMessage message) {
        uuidToChar.put(message.uuid, message.character);

        if (!message.character.isPlayer) return;
        new Thread(() -> {
            final Map<UUID, Character> players = new LinkedHashMap<>();
            for (Map.Entry<UUID, Character> e : uuidToChar.entrySet())
                if (e.getValue().isPlayer) players.put(e.getKey(), e.getValue());
            CharacterProvider.INSTANCE.save(players);
        }).start();
    }

    @SideOnly(Side.CLIENT)
    public void requestUpdate(UUID uuid, Character character) {
        CrabsNetwork.INSTANCE.sendToServer(new CharacterMessage(uuid, character));
    }

    @SideOnly(Side.CLIENT)
    public void fetch(UUID uuid, Consumer<Character> callback) {
        if (requests.containsKey(uuid)) return;
        requests.put(uuid, callback);
        CrabsNetwork.INSTANCE.sendToServer(new CharacterMessage(uuid));
    }

    @SideOnly(Side.CLIENT)
    public void onFetchResponse(CharacterMessage message) {
        final Consumer<Character> callback = requests.remove(message.uuid);
        if (callback != null) callback.accept(message.character);
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        // Удаляем энтити из базы, т.к. они одноразовые
        if (event.entity instanceof EntityPlayer) return;
        uuidToChar.remove(EntityUtils.getUuid(event.entityLiving));
    }

    @Subscribe
    public void onMiscaReload(ConfigEvent.ReloadDone event) {
        uuidToChar.putAll(CharacterProvider.INSTANCE.load());
    }
}
