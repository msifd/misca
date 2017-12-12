package msifeed.mc.misca.crabs.character;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.crabs.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public enum CharacterManager {
    INSTANCE;

    private static final Character GENERIC_CHAR = new Character(8, 8, 8, 8, 8, 8);
    private HashMap<UUID, Character> uuidToChar = new HashMap<>();

    public Character get(UUID uuid) {
        return uuidToChar.getOrDefault(uuid, GENERIC_CHAR);
    }

    public void onMessageFromClient(CharacterMessage message) {

    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        // Удаляем энтити из базы, т.к. они одноразовые
        if (event.entity instanceof EntityPlayer) return;
        uuidToChar.remove(EntityUtils.getUuid(event.entity));
    }

    public void onInit() {
    }
}
