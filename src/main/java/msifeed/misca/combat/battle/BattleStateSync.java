package msifeed.misca.combat.battle;

import msifeed.misca.Misca;
import msifeed.misca.mixins.NBTTagLongArrayMixin;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.stream.LongStream;

public class BattleStateSync {
    private static final String members = "combat.members";
    private static final String queue = "combat.queue";
    private static final String delay = "combat.delay";

    public static void sync(Battle battle) {
        final NBTTagCompound nbtMembers = encodeUuids(battle.getMembers().keySet());
        final NBTTagCompound nbtQueue = encodeUuids(battle.getQueue());
        battle.getPlayers().forEach(p -> {
            Misca.RPC.sendTo(p, members, nbtMembers);
            Misca.RPC.sendTo(p, queue, nbtQueue);
        });
    }

    static void sync(EntityPlayerMP player, Battle battle) {
        Misca.RPC.sendTo(player, members, encodeUuids(battle.getMembers().keySet()));
        Misca.RPC.sendTo(player, queue, encodeUuids(battle.getQueue()));
    }

    public static void syncQueue(Battle battle) {
        final NBTTagCompound nbt = encodeUuids(battle.getQueue());
        battle.getPlayers().forEach(p -> Misca.RPC.sendTo(p, queue, nbt));
    }

    public static void syncDelay(Battle battle) {
        battle.getPlayers().forEach(p -> Misca.RPC.sendTo(p, delay, battle.getFinishTurnDelay()));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(members)
    public void onMembersUpdate(NBTTagCompound nbt) {
        final Set<UUID> uuids = new HashSet<>();
        decodeUuidsInto(nbt, uuids);
        BattleStateClient.updateMembers(uuids);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(queue)
    public void onQueueUpdate(NBTTagCompound nbt) {
        final List<UUID> uuids = new ArrayList<>();
        decodeUuidsInto(nbt, uuids);
        BattleStateClient.updateQueue(uuids);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(delay)
    public void onDelayUpdate(long finishDelay) {
        BattleStateClient.updateFinishDelay(finishDelay);
    }

    static NBTTagCompound encodeUuids(Collection<UUID> delta) {
        final long[] ids = delta.stream()
                .flatMapToLong(uuid -> LongStream.of(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()))
                .toArray();

        final NBTTagCompound nbt = new NBTTagCompound();
        if (ids.length > 0)
            nbt.setTag("ids", new NBTTagLongArray(ids));
        return nbt;
    }

    private static void decodeUuidsInto(NBTTagCompound nbt, Collection<UUID> uuids) {
        if (!nbt.hasKey("ids", 12)) return; // 12 - NBTTagLongArray

        final NBTTagLongArray idsArray = (NBTTagLongArray) nbt.getTag("ids");
        final long[] ids = ((NBTTagLongArrayMixin) idsArray).getData();
        if (ids.length < 2) return;

        for (int i = 0; i < ids.length; i += 2) {
            uuids.add(new UUID(ids[i], ids[i + 1]));
        }
    }
}
