package msifeed.mc.misca.tweaks;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.HashMap;
import java.util.Map;

public class SpawnInvincibility {
    private Map<EntityPlayer, ChunkCoordinates> ignoredPlayers = new HashMap<>();

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entity;
        ignoredPlayers.put(player, player.getPlayerCoordinates());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttack(LivingAttackEvent event) {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }
        if (ignoredPlayers.containsKey(event.entity)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entity;
        ChunkCoordinates init_pos = ignoredPlayers.get(player);
        if (init_pos == null) return;
        if (init_pos.getDistanceSquaredToChunkCoordinates(player.getPlayerCoordinates()) > 4) {
            ignoredPlayers.remove(player);
        }
    }
}
