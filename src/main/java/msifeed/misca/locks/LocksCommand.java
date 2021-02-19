package msifeed.misca.locks;

import msifeed.misca.MiscaPerms;
import msifeed.misca.locks.cap.LockAccessor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class LocksCommand extends CommandBase {
    @Override
    public String getName() {
        return "locks";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/locks <toggle add remove> <key>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return MiscaPerms.userLevel(sender, "misca.locks");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, "toggle", "add", "remove");
        else
            return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP) || args.length < 1) return;

        final EntityPlayerMP player = (EntityPlayerMP) sender;
        switch (args[0]) {
            case "toggle":
                if (args.length < 2) {
                    sendStatus(player, "Usage: /locks toggle <key>", TextFormatting.RED);
                    return;
                }
                toggle(player, parseInt(args[1]));
                break;
            case "add":
                if (args.length < 2) {
                    sendStatus(player, "Usage: /locks add <key>", TextFormatting.RED);
                    return;
                }
                addLock(player, parseInt(args[1]));
                break;
            case "remove":
                removeLock(player);
                break;
            default:
                break;
        }
    }

    private static void toggle(EntityPlayerMP player, int key) {
        final BlockPos pos = rayTracePos(player);
        if (pos == null) return;

        if (Locks.toggleLock(player.world, pos, key)) {
            final boolean locked = LockAccessor.isLocked(player.world, pos);
            sendStatus(player, "Block " + (locked ? "locked" : "opened"), TextFormatting.GREEN);
        } else {
            sendStatus(player, "Failed to toggle lock", TextFormatting.RED);
        }
    }

    private static void addLock(EntityPlayerMP player, int secret) {
        final BlockPos pos = rayTracePos(player);
        if (pos == null) return;

        if (Locks.addLock(player.world, pos, secret))
            sendStatus(player, "Lock added", TextFormatting.GREEN);
        else
            sendStatus(player, "Failed to add lock", TextFormatting.RED);
    }

    private static void removeLock(EntityPlayerMP player) {
        final BlockPos pos = rayTracePos(player);
        if (pos == null) return;

        if (Locks.removeLock(player.world, pos))
            sendStatus(player, "Lock removed", TextFormatting.GREEN);
        else
            sendStatus(player, "Failed to remove lock", TextFormatting.RED);
    }

    private static BlockPos rayTracePos(EntityPlayerMP player) {
        final RayTraceResult ray = ForgeHooks.rayTraceEyes(player, 5);
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK) {
            sendStatus(player, "Look at block!", TextFormatting.RED);
            return null;
        }

        return ray.getBlockPos();
    }

    private static void sendStatus(EntityPlayerMP player, String message, TextFormatting color) {
        final ITextComponent te = new TextComponentString(message);
        te.getStyle().setColor(color);
        player.sendStatusMessage(te, true);
    }
}
