package msifeed.misca.environ;

import msifeed.misca.Misca;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class EnvironWorldData extends WorldSavedData {
    private static final String DATA_NAME = Misca.MODID + ".environ";

    long rainAcc = 0;

    public static EnvironWorldData get(World world) {
        final MapStorage storage = world.getPerWorldStorage();

        EnvironWorldData instance = (EnvironWorldData) storage.getOrLoadData(EnvironWorldData.class, DATA_NAME);
        if (instance == null) {
            instance = new EnvironWorldData();
            storage.setData(DATA_NAME, instance);
        }

        return instance;
    }

    public EnvironWorldData() {
        super(DATA_NAME);
    }

    public EnvironWorldData(String name) {
        super(name);
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        rainAcc = nbt.getLong("rain");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setLong("rain", rainAcc);
        return compound;
    }
}
