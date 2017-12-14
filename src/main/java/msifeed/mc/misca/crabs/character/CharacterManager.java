package msifeed.mc.misca.crabs.character;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.misca.crabs.CrabsNetwork;
import msifeed.mc.misca.crabs.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public enum CharacterManager {
    INSTANCE;

    private static final Character GENERIC_CHAR = new Character(8, 8, 8, 8, 8, 8);
    private HashMap<UUID, Character> uuidToChar = new HashMap<>();

    public void onInit() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public Character get(UUID uuid) {
        return uuidToChar.putIfAbsent(uuid, GENERIC_CHAR);
    }

    @SideOnly(Side.CLIENT)
    public Character request(EntityLivingBase entity) {
        final UUID uuid = EntityUtils.getUuid(entity);
        final Character character = uuidToChar.get(uuid);
        if (character == null) {
            CrabsNetwork.INSTANCE.sendToServer(new CharacterMessage(uuid));
        }
        return character;
    }

    public void onCharacterReceive(CharacterMessage message) {
        uuidToChar.put(message.uuid, message.character);
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        // Удаляем энтити из базы, т.к. они одноразовые
        if (event.entity instanceof EntityPlayer) return;
        uuidToChar.remove(EntityUtils.getUuid(event.entityLiving));
    }
}
