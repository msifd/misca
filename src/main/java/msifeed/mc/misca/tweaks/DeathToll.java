package msifeed.mc.misca.tweaks;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class DeathToll {
    private String[] illnesses;
    private File logFile;

    public void preInit() {
        illnesses = MiscaUtils.l10n("misca.death_toll.illnesses").split(";");

        final File logsDir = new File(ConfigManager.config_dir,"logs");
        logsDir.mkdirs();
        logFile = new File(logsDir,"player_death.log");
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        String death_source = event.source.damageType;
        ChunkCoordinates coords = player.getPlayerCoordinates();
        String illness = getRandomIllness();
        String msg = String.format("[%s] '%s' died from '%s' at (%s: %d %d %d). Random illness '%s'\n",
                LocalDateTime.now().toString(),
                player.getDisplayName(),
                death_source,
                player.getEntityWorld().getWorldInfo().getWorldName(),
                coords.posX, coords.posY, coords.posZ,
                illness);

        Tweaks.logger.info(msg);
        new Thread(() -> {
            try {
                Files.write(logFile.toPath(), msg.getBytes(UTF_8), APPEND, CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        String chat_msg = String.format("%s \"%s\"!", MiscaUtils.l10n("misca.death_toll.intro"), illness);
        player.addChatMessage(new ChatComponentText(chat_msg));
    }

    private String getRandomIllness() {
        return illnesses[new Random().nextInt(illnesses.length)];
    }
}
