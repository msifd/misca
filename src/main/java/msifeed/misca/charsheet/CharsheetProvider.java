package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class CharsheetProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ICharsheet.class)
    public static Capability<ICharsheet> CAP = null;

    private final Capability.IStorage<ICharsheet> storage = CAP.getStorage();
    private final ICharsheet instance = Objects.requireNonNull(CAP.getDefaultInstance());

    public static void preInit() {
        CapabilityManager.INSTANCE.register(ICharsheet.class, new CharsheetStorage(), Charsheet::new);
        MinecraftForge.EVENT_BUS.register(new CharsheetEventHandler());
        Misca.RPC.register(new CharsheetSync());
    }

    @Nonnull
    public static ICharsheet get(EntityLivingBase entity) {
        return Objects.requireNonNull(entity.getCapability(CharsheetProvider.CAP, null));
    }

    @Nonnull
    public static ICharsheet getOr(EntityLivingBase entity, ICharsheet def) {
        final ICharsheet cs = entity.getCapability(CharsheetProvider.CAP, null);
        return cs != null ? cs : def;
    }

    public static NBTTagCompound encode(ICharsheet charsheet) {
        return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, charsheet, null);
    }

    public static ICharsheet decode(NBTTagCompound nbt) {
        final ICharsheet cs = CAP.getDefaultInstance();
        CAP.getStorage().readNBT(CAP, cs, null, nbt);
        return cs;
    }

    public CharsheetProvider(boolean isPlayer) {
        if (isPlayer)
            instance.markPlayer();
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

    @Override
    public NBTBase serializeNBT() {
        return storage.writeNBT(CAP, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        storage.readNBT(CAP, instance, null, nbt);
    }
}
