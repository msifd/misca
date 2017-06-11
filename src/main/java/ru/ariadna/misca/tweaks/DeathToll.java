package ru.ariadna.misca.tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import ru.ariadna.misca.config.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DeathToll {

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer))
            return;

        PrintWriter writer;
        try {
            File log_file = new File(ConfigManager.config_dir, "player_death.log");
            writer = new PrintWriter(new FileWriter(log_file, true));
        } catch (IOException e) {
            Tweaks.logger.error("Cannot create death log file! : " + e.getMessage());
            return;
        }

        String timestamp = new SimpleDateFormat("dd.MM.yy-HH:mm:ss").format(new Date());
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        String death_source = event.source.damageType;
        ChunkCoordinates coords = player.getPlayerCoordinates();
        String illness = getRandomIllness();
        String msg = String.format("[%s] Player '%s' died from '%s' at (%s) %d-%d-%d. Random illness '%s'\n",
                timestamp,
                player.getDisplayName(),
                death_source,
                player.getEntityWorld().getWorldInfo().getWorldName(),
                coords.posX, coords.posY, coords.posZ,
                illness);

        writer.append(msg);
        writer.flush();
        writer.close();

        String chat_msg = "Вы заболели " + illness;
        player.addChatMessage(new ChatComponentText(chat_msg));
    }

    private String getRandomIllness() {
        final String[] stats = {
                "мышечными спазмами", "Немощью", "Ломкостью",
                "неуклюжестью", "нарушением моторики", "Трясучкой",
                "потерей чувств", "Неразборчивостью", "Расфокусировкой",
                "амнезией", "Технологической слепотой", "забывчивостью",
                "Бесхребетностью", "Раболепием", "подавлением иммунитета",
                "Душевными метаниями", "Магочувствительностью"};
        Random rand = new Random();
        return stats[rand.nextInt(stats.length)];
    }
}
