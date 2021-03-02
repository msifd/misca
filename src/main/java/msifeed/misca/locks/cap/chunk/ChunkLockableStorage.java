package msifeed.misca.locks.cap.chunk;

import msifeed.misca.locks.cap.lock.ILockable;
import msifeed.misca.locks.cap.lock.LockableProvider;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.Map;

public class ChunkLockableStorage implements Capability.IStorage<IChunkLockable> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IChunkLockable> capability, IChunkLockable instance, EnumFacing side) {
        Capability.IStorage<ILockable> ls = LockableProvider.CAP.getStorage();
        final NBTTagList list = new NBTTagList();
        for (Map.Entry<BlockPos, ILockable> e : instance.getLocks().entrySet()) {
            final NBTTagCompound nbt = new NBTTagCompound();
            nbt.setLong(Tag.pos, e.getKey().toLong());
            nbt.setTag(Tag.lock, ls.writeNBT(LockableProvider.CAP, e.getValue(), null));
            list.appendTag(nbt);
        }

        final NBTTagCompound root = new NBTTagCompound();
        root.setTag(Tag.locks, list);
        return root;
    }

    @Override
    public void readNBT(Capability<IChunkLockable> capability, IChunkLockable instance, EnumFacing side, NBTBase nbtBase) {
        Capability.IStorage<ILockable> ls = LockableProvider.CAP.getStorage();
        final NBTTagCompound root = (NBTTagCompound) nbtBase;
        final NBTTagList list = root.getTagList(Tag.locks, 10); // 10 - NBTTagCompound
        for (int i = 0; i < list.tagCount(); i++) {
            final NBTTagCompound nbt = list.getCompoundTagAt(i);
            final ILockable lock = LockableProvider.CAP.getDefaultInstance();
            if (!nbt.hasKey(Tag.lock, 10)) continue;
            ls.readNBT(LockableProvider.CAP, lock, null, nbt.getTag(Tag.lock));
            instance.addLock(BlockPos.fromLong(nbt.getLong(Tag.pos)), lock);
        }
    }

    private static class Tag {
        private static final String locks = "locks";
        private static final String pos = "pos";
        private static final String lock = "lock";
    }
}
