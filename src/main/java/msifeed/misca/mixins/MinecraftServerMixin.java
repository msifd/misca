package msifeed.misca.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Redirect(method = "getTabCompletions", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;getOnlinePlayerNames()[Ljava/lang/String;"))
    public String[] getNamesForTabCompletions(PlayerList playerList) {
        final PlayerListMixin mixin = (PlayerListMixin) playerList;
        final List<EntityPlayerMP> players = mixin.getPlayerEntityList();

        final String[] names = new String[players.size()];
        for (int i = 0; i < players.size(); ++i)
            names[i] = players.get(i).getDisplayNameString();
        return names;
    }
}
