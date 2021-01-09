package msifeed.misca.combat.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class CombatantStorage implements Capability.IStorage<ICombatant> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ICombatant> capability, ICombatant instance, EnumFacing side) {
        final NBTTagCompound nbt = new NBTTagCompound();
        if (instance.isInBattle()) {
            nbt.setLong("bid", instance.getBattleId());
            nbt.setFloat("ap", (float) instance.getActionPoints());
            nbt.setFloat("apo", (float) instance.getActionPointsOverhead());
            nbt.setDouble("posX", instance.getPosition().x);
            nbt.setDouble("posY", instance.getPosition().y);
            nbt.setDouble("posZ", instance.getPosition().z);
        }
        if (instance.getTrainingHealth() != 0)
            nbt.setFloat("trainHP", instance.getTrainingHealth());
        if (instance.getNeutralDamage() != 0)
            nbt.setFloat("neutralDmg", instance.getNeutralDamage());
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
        instance.setNeutralDamage(nbt.getFloat("neutralDmg"));
    }
}
