package msifeed.misca.combat.cap;

import msifeed.misca.Misca;
import msifeed.misca.MiscaPerms;
import msifeed.misca.combat.CharAttribute;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class CombatantSync {
    private static final String sync = "combat.sync";
    private static final String syncAp = "combat.syncAp";
    private static final String syncNeutral = "combat.syncNeutral";
    private static final String postAttrs = "combat.postAttrs";

    public static void sync(EntityPlayerMP receiver, EntityLivingBase target) {
        final NBTTagCompound nbt = CombatantProvider.encode(CombatantProvider.get(target));
        Misca.RPC.sendTo(receiver, sync, target.getUniqueID(), nbt);
    }

    public static void sync(EntityLivingBase target) {
        final NBTTagCompound nbt = CombatantProvider.encode(CombatantProvider.get(target));
        Misca.RPC.sendToAllAround(target, sync, target.getUniqueID(), nbt);
    }

    public static void syncAp(EntityLivingBase target) {
        final ICombatant com = CombatantProvider.get(target);
        Misca.RPC.sendToAllAround(target, syncAp, target.getUniqueID(), com.getActionPoints(), com.getActionPointsOverhead());
    }

    public static void syncNeutralDamage(EntityLivingBase target) {
        final ICombatant com = CombatantProvider.get(target);
        Misca.RPC.sendToAllAround(target, syncNeutral, target.getUniqueID(), com.getNeutralDamage());
    }

    public static void postAttrs(EntityLivingBase target, int[] attrs) {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setIntArray("attrs", attrs);
        Misca.RPC.sendToServer(postAttrs, target.getUniqueID(), nbt);
    }

    // Server side

    @RpcMethodHandler(postAttrs)
    public void onPostAttrs(RpcContext ctx, UUID uuid, NBTTagCompound nbt) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        if (!MiscaPerms.isGameMaster(sender)) return;

        final int[] attrs = nbt.getIntArray("attrs");

        sender.world.loadedEntityList.stream()
                .filter(e -> e.getUniqueID().equals(uuid))
                .findAny()
                .filter(e -> e instanceof EntityLivingBase)
                .ifPresent(e -> {
                    updateAttrs((EntityLivingBase) e, attrs);
                    sender.sendStatusMessage(new TextComponentString("Attrs updated"), true);
                });
    }

    private void updateAttrs(EntityLivingBase target, int[] attrs) {
        for (CharAttribute attr : CharAttribute.values()) {
            final int value = attrs[attr.ordinal()];
            target.getEntityAttribute(attr.attribute).setBaseValue(value);
        }
    }

    // Client side

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(sync)
    public void onSync(UUID uuid, NBTTagCompound nbt) {
        Minecraft.getMinecraft().world.loadedEntityList.stream()
                .filter(e -> e.getUniqueID().equals(uuid))
                .findAny()
                .filter(e -> e instanceof EntityLivingBase)
                .ifPresent(e -> {
                    final ICombatant com = CombatantProvider.get((EntityLivingBase) e);
                    com.replaceWith(CombatantProvider.decode(nbt));
                });
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(syncAp)
    public void onSyncAp(UUID uuid, double ap, double apo) {
        final EntityLivingBase entity = BattleStateClient.STATE.getMember(uuid);
        if (entity != null) {
            final ICombatant com = CombatantProvider.get(entity);
            com.setActionPoints(ap);
            com.setActionPointsOverhead(apo);
        }
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(syncNeutral)
    public void onSyncNeutral(UUID uuid, float neutralDamage) {
        final EntityLivingBase entity = BattleStateClient.STATE.getMember(uuid);
        if (entity != null) {
            final ICombatant com = CombatantProvider.get(entity);
            com.setNeutralDamage(neutralDamage);
        }
    }
}
