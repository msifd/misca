package msifeed.mc.misca.tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.utils.FileLogger;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class DeathToll {
    private String[] illnesses;

    public void preInit() {
        illnesses = MiscaUtils.l10n("misca.death_toll.illnesses").split(";");
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer))
            return;

        final EntityPlayer player = (EntityPlayer) event.entityLiving;
        final String death_source = event.source.damageType;
        final ChunkCoordinates coords = player.getPlayerCoordinates();
        final String illness = getRandomIllness();
        final String msg = String.format("'%s' died from '%s' at (%s: %d %d %d). Random illness '%s'",
                player.getDisplayName(),
                death_source,
                player.getEntityWorld().getWorldInfo().getWorldName(),
                coords.posX, coords.posY, coords.posZ,
                illness);
        FileLogger.log("player_death", msg);

        final String chat_msg = String.format("%s \"%s\"!", MiscaUtils.l10n("misca.death_toll.intro"), illness);
        player.addChatMessage(new ChatComponentText(chat_msg));
    }

    private String getRandomIllness() {
        return illnesses[new Random().nextInt(illnesses.length)];
    }
}
