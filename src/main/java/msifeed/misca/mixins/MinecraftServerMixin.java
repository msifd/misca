package msifeed.misca.mixins;

import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
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
        for (int i = 0; i < players.size(); ++i) {
            final EntityPlayerMP p = players.get(i);
            final ICharsheet cs = CharsheetProvider.get(p);
            names[i] = cs.getName().isEmpty() ? p.getDisplayNameString() : cs.getName();
        }
        return names;
    }
}
