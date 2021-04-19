package msifeed.misca.cmd;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

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
        final Chunk chunk = world.getChunk(pos);
        boolean blocks = true;

        while (pos.getY() < world.getActualHeight()) {
            if (chunk.getBlockState(pos).getMaterial().blocksMovement()) {
                blocks = true;
                pos.setY(pos.getY() + 1);
            } else if (!blocks) {
                pos.setY(pos.getY() - 1);
                break;
            } else {
                blocks = false;
                pos.setY(pos.getY() + 1);
            }
        }

        player.setPositionAndUpdate(player.posX, pos.getY(), player.posZ);
    }
}
