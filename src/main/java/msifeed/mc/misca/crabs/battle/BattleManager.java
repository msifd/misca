package msifeed.mc.misca.crabs.battle;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.UUID;

public enum BattleManager {
    INSTANCE;

    private Logger logger = LogManager.getLogger("Crabs.Battle");
    private HashMap<UUID, FighterContext> uuidToContext = new HashMap<>();

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.entity;
        FighterContext context = uuidToContext.get(player.getUniqueID());
        if (context == null) return;

        context.player = player;

        if (player instanceof EntityPlayerMP)
            BattleNetwork.INSTANCE.notifyContext((EntityPlayerMP) player, context);
    }
}
