package msifeed.misca.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PlayerList.class)
public interface PlayerListMixin {
    @Accessor
    List<EntityPlayerMP> getPlayerEntityList();
}
