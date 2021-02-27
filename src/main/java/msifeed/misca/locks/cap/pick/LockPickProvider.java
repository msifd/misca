package msifeed.misca.locks.cap.pick;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LockPickProvider implements ICapabilityProvider {
    @CapabilityInject(ILockPick.class)
    public static Capability<ILockPick> CAP = null;

    private final ILockPick instance = new LockPickImpl();

    @Nullable
    public static ILockPick get(ItemStack stack) {
        return stack.getCapability(LockPickProvider.CAP, null);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CAP ? CAP.cast(this.instance) : null;
    }
}
