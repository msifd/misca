package msifeed.misca.combat.cap;

import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class CombatantClientRpc {
    public static final String post = "combatant.post";
    public static final String sync = "combatant.sync";
    public static final String syncSelf = "combatant.syncSelf";

    @RpcMethodHandler(syncSelf)
    public void onSyncSelf(RpcContext ctx, NBTTagCompound nbt) {
        update(Minecraft.getMinecraft().player, nbt);
    }

    @RpcMethodHandler(sync)
    public void onSync(RpcContext ctx, UUID uuid, NBTTagCompound nbt) {
        Minecraft.getMinecraft().world.loadedEntityList.stream()
                .filter(e -> e.getUniqueID().equals(uuid))
                .findAny()
                .filter(e -> e instanceof EntityLivingBase)
                .ifPresent(e -> update((EntityLivingBase) e, nbt));
    }

    private void update(EntityLivingBase target, NBTTagCompound nbt) {
        CombatantProvider.get(target).replaceWith(CombatantProvider.decode(nbt));
    }
}
