package msifeed.mc.misca.tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.MiscaUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.HashMap;
import java.util.Map;

public class HealthNotification {
    private Map<EntityPlayer, Integer> notified = new HashMap<>();

    private static void sendNear(EntityPlayer center, String msg) {
        center.getEntityWorld().playerEntities.stream()
                .filter(p -> ((EntityPlayer) p).getDistanceToEntity(center) <= 15)
                .forEach(o -> ((EntityPlayerMP) o).addChatMessage(new ChatComponentText(msg)));
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        float ratio = player.getHealth() / player.getMaxHealth();
        Integer last = notified.get(player);
        if (last == null) last = 0;

        if (ratio > 0.75)
            notified.remove(player);
        else if (ratio > 0.5 && ratio <= 0.75 && last < 1) {
            notified.put(player, 1);
            sendNear(player, String.format("%s %s.", player.getDisplayName(), MiscaUtils.l10n("misca.health_note.wound")));
        } else if (ratio > 0.25 && ratio <= 0.5 && last < 2) {
            notified.put(player, 2);
            sendNear(player, String.format("%s %s.", player.getDisplayName(), MiscaUtils.l10n("misca.health_note.injury")));
        } else if (ratio <= 0.25 && last < 3) {
            notified.put(player, 3);
            sendNear(player, String.format("%s %s.", player.getDisplayName(), MiscaUtils.l10n("misca.health_note.dying")));
        }
    }
}
