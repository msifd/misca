package msifeed.misca.cmd;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

public class UnstuckCommand extends CommandBase {
    @Override
    public String getName() {
        return "unstuck";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        final EntityPlayer player = (EntityPlayer) sender;
        final World world = ((EntityPlayer) sender).world;

        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(player.getPosition());
        final ChunkProviderServer provider = (ChunkProviderServer) world.getChunkProvider();

        provider.loadChunk(pos.getX() >> 4, pos.getZ() >> 4, () -> {
            final Chunk chunk = world.getChunkFromBlockCoords(pos);
            boolean platform = false;
            boolean blocks = true;

            final int topY = world.getTopSolidOrLiquidBlock(pos).getY();

            pos.setY(pos.getY() - 3);
            while (pos.getY() < topY) {
                if (chunk.getBlockState(pos).getMaterial().blocksMovement()) {
                    platform = true;
                    blocks = true;
                    pos.setY(pos.getY() + 1);
                } else if (!blocks && platform) {
                    pos.setY(pos.getY() - 1);
                    break;
                } else {
                    blocks = false;
                    pos.setY(pos.getY() + 1);
                }
            }

            player.setPositionAndUpdate(player.prevPosX, pos.getY(), player.prevPosZ);
        });
    }
}
