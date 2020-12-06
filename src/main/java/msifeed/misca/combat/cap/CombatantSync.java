package msifeed.misca.combat.cap;

import msifeed.misca.Misca;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class CombatantSync {
    public static final String sync = "combatant.sync";
    public static final String syncSelf = "combatant.syncSelf";

    public static void sync(EntityPlayerMP receiver, EntityLivingBase target) {
        final NBTTagCompound nbt = CombatantProvider.encode(CombatantProvider.get(target));
        if (receiver == target)
            Misca.RPC.sendTo(receiver, CombatantSync.syncSelf, nbt);
        else
            Misca.RPC.sendTo(receiver, CombatantSync.sync, target.getUniqueID(), nbt);
    }

    public static void sync(EntityLivingBase target) {
        final NBTTagCompound nbt = CombatantProvider.encode(CombatantProvider.get(target));
        if (target instanceof EntityPlayerMP)
            Misca.RPC.sendTo((EntityPlayerMP) target, CombatantSync.syncSelf, nbt);
        Misca.RPC.sendToAllTracking(target, CombatantSync.sync, target.getUniqueID(), nbt);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(syncSelf)
    public void onSyncSelf(RpcContext ctx, NBTTagCompound nbt) {
        final ICombatant com = update(Minecraft.getMinecraft().player, nbt);
        if (!com.isInBattle())
            BattleStateClient.clear();
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(sync)
    public void onSync(RpcContext ctx, UUID uuid, NBTTagCompound nbt) {
        Minecraft.getMinecraft().world.loadedEntityList.stream()
                .filter(e -> e.getUniqueID().equals(uuid))
                .findAny()
                .filter(e -> e instanceof EntityLivingBase)
                .ifPresent(e -> update((EntityLivingBase) e, nbt));
    }

    @SideOnly(Side.CLIENT)
    private ICombatant update(EntityLivingBase target, NBTTagCompound nbt) {
        final ICombatant com = CombatantProvider.get(target);
        com.replaceWith(CombatantProvider.decode(nbt));
        return com;
    }
}
