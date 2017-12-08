package msifeed.mc.misca.crabs.character;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.crabs.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public enum CharacterManager {
    INSTANCE;

    private HashMap<UUID, Character> uuidToChar = new HashMap<>();

    public Character get(UUID uuid) {
        // TODO заменить поиском по настоящей базе игроков
        Character test = new Character();
        test.fill(1, 2, 3, 15, 5, 6);
        return test;

//        return uuidToChar.get(uuid);
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        // Удаляем энтити из базы, т.к. они одноразовые
        if (event.entity instanceof EntityPlayer) return;
        uuidToChar.remove(EntityUtils.getUuid(event.entity));
    }
}
