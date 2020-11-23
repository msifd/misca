package msifeed.misca.combat.cap;

import msifeed.misca.Misca;
import msifeed.misca.combat.Combat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import javax.annotation.Nullable;

public class CombatantHandler implements Capability.IStorage<ICombatant> {
    public static final ResourceLocation CAP = new ResourceLocation(Misca.MODID, "combat");

    public void preInit() {
        CapabilityManager.INSTANCE.register(ICombatant.class, this, Combatant::new);
        MinecraftForge.EVENT_BUS.register(this);

        if (FMLCommonHandler.instance().getSide().isClient())
            Misca.RPC.register(new CombatantClientRpc());
    }

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ICombatant> capability, ICombatant instance, EnumFacing side) {
        final NBTTagCompound nbt = new NBTTagCompound();
        if (instance.isInBattle()) {
            nbt.setLong("bid", instance.getBattleId());
            nbt.setFloat("ap", instance.getActionPoints());
            nbt.setFloat("apo", instance.getActionPointsOverhead());
            nbt.setDouble("posX", instance.getPosition().x);
            nbt.setDouble("posY", instance.getPosition().y);
            nbt.setDouble("posZ", instance.getPosition().z);
        }
        if (instance.getTrainingHealth() != 0)
            nbt.setFloat("trainHP", instance.getTrainingHealth());
        return nbt;
    }

    @Override
    public void readNBT(Capability<ICombatant> capability, ICombatant instance, EnumFacing side, NBTBase nbtBase) {
        final NBTTagCompound nbt = (NBTTagCompound) nbtBase;
        instance.setBattleId(nbt.getLong("bid"));
        instance.setActionPoints(nbt.getFloat("ap"));
        instance.setActionPointsOverhead(nbt.getFloat("apo"));
        instance.setPosition(new Vec3d(
                nbt.getDouble("posX"),
                nbt.getDouble("posY"),
                nbt.getDouble("posZ")
        ));
        instance.setTrainingHealth(nbt.getFloat("trainHP"));
    }

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityLivingBase)
            event.addCapability(CAP, new CombatantProvider());
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        final ICombatant original = CombatantProvider.get(event.getOriginal());
        final ICombatant cloned = CombatantProvider.get(event.getEntityPlayer());
        cloned.replaceWith(original);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.world.isRemote) {
            Combat.MANAGER.rejoinToBattle(event.player);
            sync((EntityPlayerMP) event.player, event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.player.world.isRemote) {
            Combat.MANAGER.leaveFromBattle(event.player);
            CombatantProvider.get(event.player).reset();
            sync((EntityPlayerMP) event.player, event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.player.world.isRemote) {
            Combat.MANAGER.leaveFromBattle(event.player);
            CombatantProvider.get(event.player).reset();
            sync((EntityPlayerMP) event.player, event.player);
        }
    }

    @SubscribeEvent
    public void onTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityLivingBase)
            sync((EntityPlayerMP) event.getEntityPlayer(), (EntityLivingBase) event.getTarget());
    }

    private void sync(EntityPlayerMP receiver, EntityLivingBase target) {
        final NBTTagCompound nbt = CombatantProvider.encode(CombatantProvider.get(target));
        if (receiver == target)
            Misca.RPC.sendTo(receiver, CombatantClientRpc.syncSelf, nbt);
        else
            Misca.RPC.sendTo(receiver, CombatantClientRpc.sync, target.getUniqueID(), nbt);
    }

    public static void sync(EntityLivingBase target) {
        final NBTTagCompound nbt = CombatantProvider.encode(CombatantProvider.get(target));
        if (target instanceof EntityPlayerMP)
            Misca.RPC.sendTo((EntityPlayerMP) target, CombatantClientRpc.syncSelf, nbt);
        Misca.RPC.sendToAllTracking(target, CombatantClientRpc.sync, target.getUniqueID(), nbt);
    }
}
