package msifeed.misca.combat;

import msifeed.misca.Misca;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.client.GuiCombatOverlay;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CombatEventSync {
    private static final String method = "combat.event";

    public static void send(Battle battle, EntityLivingBase src, CombatEvent event) {
        battle.getPlayers()
                .forEach(p -> Misca.RPC.sendTo(p, method, src.getEntityId(), event.ordinal()));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(method)
    public void onEvent(int eid, int eventOrd) {
        final Entity entity = Minecraft.getMinecraft().world.getEntityByID(eid);
        if (!(entity instanceof EntityLivingBase)) return;

        final CombatEvent event = CombatEvent.values()[eventOrd];
        final GuiCombatOverlay.TimedEvent te = new GuiCombatOverlay.TimedEvent();
        te.who = getName((EntityLivingBase) entity);
        te.isMe = Minecraft.getMinecraft().player.getEntityId() == eid;
        te.event = event;

        GuiCombatOverlay.postEvent(te);
    }

    private static String getName(EntityLivingBase atk) {
        if (atk instanceof EntityPlayer)
            return ((EntityPlayer) atk).getDisplayNameString();
        else
            return atk.getName();
    }
}
