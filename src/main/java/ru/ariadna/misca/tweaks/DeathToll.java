package ru.ariadna.misca.tweaks;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.config.ConfigManager;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DeathToll {
    private static String[] illnesses;

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer))
            return;

        PrintWriter writer;
        try {
            File log_file = new File(ConfigManager.config_dir, "player_death.log");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(log_file, true), Charsets.UTF_8);
            writer = new PrintWriter(outputStreamWriter);
        } catch (IOException e) {
            Tweaks.logger.error("Cannot create death log file! : " + e.getMessage());
            return;
        }

        String timestamp = new SimpleDateFormat("dd.MM.yy-HH:mm:ss").format(new Date());
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        String death_source = event.source.damageType;
        ChunkCoordinates coords = player.getPlayerCoordinates();
        String illness = getRandomIllness();
        String msg = String.format("[%s] Player '%s' died from '%s' at (%s: %d %d %d). Random illness '%s'\n",
                timestamp,
                player.getDisplayName(),
                death_source,
                player.getEntityWorld().getWorldInfo().getWorldName(),
                coords.posX, coords.posY, coords.posZ,
                illness);

        writer.append(msg);
        writer.flush();
        writer.close();
        Tweaks.logger.info(msg);

        String chat_msg = String.format("%s \"%s\"!", MiscaUtils.l10n("misca.death_toll.intro"), illness);
        player.addChatMessage(new ChatComponentText(chat_msg));
    }

    private String getRandomIllness() {
        if (illnesses == null) illnesses = MiscaUtils.l10n("misca.death_toll.illnesses").split(";");
        return illnesses[new Random().nextInt(illnesses.length)];
    }
}
