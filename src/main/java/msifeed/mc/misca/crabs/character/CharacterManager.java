package msifeed.mc.misca.crabs.character;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.misca.crabs.CrabsNetwork;
import msifeed.mc.misca.crabs.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public enum CharacterManager {
    INSTANCE;

    private static final Character GENERIC_CHAR = new Character(new int[]{8, 8, 8, 8, 8, 8});
    private HashMap<UUID, Character> uuidToChar = new HashMap<>();

    public void onInit() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        uuidToChar.putAll(CharacterProvider.INSTANCE.load());
    }

    public Character get(UUID uuid) {
        return uuidToChar.getOrDefault(uuid, GENERIC_CHAR);
    }

    @SideOnly(Side.CLIENT)
    public void request(UUID uuid) {
        CrabsNetwork.INSTANCE.sendToServer(new CharacterMessage(uuid));
    }

    @SideOnly(Side.CLIENT)
    public void requestUpdate(UUID uuid, Character character) {
        CrabsNetwork.INSTANCE.sendToServer(new CharacterMessage(uuid, character));
    }

    @SideOnly(Side.CLIENT)
    public void discard(UUID uuid) {
        uuidToChar.remove(uuid);
    }

    @SideOnly(Side.CLIENT)
    public void onClientReceive(CharacterMessage message) {
        uuidToChar.put(message.uuid, message.character);
    }

    public void onServerReceive(CharacterMessage message) {
        uuidToChar.put(message.uuid, message.character);
        new Thread(() -> CharacterProvider.INSTANCE.save(uuidToChar)).start();
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        // Удаляем энтити из базы, т.к. они одноразовые
        if (event.entity instanceof EntityPlayer) return;
        uuidToChar.remove(EntityUtils.getUuid(event.entityLiving));
    }
}
