package msifeed.misca.locks;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

public class LocksCommand extends CommandBase {
    @Override
    public String getName() {
        return "locks";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/locks < toggle <key> | add <key> | remove [key] >";
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
                toggle(player, args[1]);
                break;
            case "add":
                if (args.length < 2) {
                    sendStatus(player, "Usage: /locks add <key>", TextFormatting.RED);
                    return;
                }
                addLock(player, args[1]);
                break;
            case "remove":
                removeLock(player, args.length > 1 ? args[1] : null);
                break;
            default:
                break;
        }
    }

    private static void toggle(EntityPlayerMP player, String key) {
        final TileEntity tile = rayTraceTile(player);
        if (tile == null) return;

        final ILockable lock = LockableProvider.get(tile);
        if (lock == null) return;

        if (!lock.hasSecret()) {
            sendStatus(player, "Block is not locked!", TextFormatting.RED);
            return;
        }

        if (!lock.canOpenWith(key)) {
            sendStatus(player, "Wrong key!", TextFormatting.RED);
            return;
        }

        lock.setLocked(!lock.isLocked());
        tile.markDirty();

        sendStatus(player, "Block " + (lock.isLocked() ? " locked" : "opened"), TextFormatting.GREEN);
    }

    private static void addLock(EntityPlayerMP player, String secret) {
        // TODO: add tiles to doors
        final TileEntity tile = rayTraceTile(player);
        if (tile == null) return;

        final ILockable lock = LockableProvider.get(tile);
        if (lock == null) return;

        if (lock.isLocked()) {
            sendStatus(player, "Block is locked!", TextFormatting.RED);
            return;
        }

        lock.setLocked(false);
        lock.setSecret(secret);
        tile.markDirty();

        sendStatus(player, "Lock added", TextFormatting.GREEN);
    }

    private static void removeLock(EntityPlayerMP player, @Nullable String key) {
        final TileEntity tile = rayTraceTile(player);
        if (tile == null) return;

        final ILockable lock = LockableProvider.get(tile);
        if (lock == null) return;

        if (lock.isLocked() && lock.hasSecret()) {
            if (key == null) {
                sendStatus(player, "Block is locked!", TextFormatting.RED);
                return;
            } else if (!lock.canOpenWith(key)){
                sendStatus(player, "Wrong key!", TextFormatting.RED);
                return;
            }
        }

        // TODO: remove tile from doors (maybe)
        lock.setLocked(false);
        lock.setSecret(ILockable.NO_SECRET);
        tile.markDirty();

        sendStatus(player, "Lock removed", TextFormatting.GREEN);
    }

    private static TileEntity rayTraceTile(EntityPlayerMP player) {
        final RayTraceResult ray = ForgeHooks.rayTraceEyes(player, 5);
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK) {
            sendStatus(player, "Look at block!", TextFormatting.RED);
            return null;
        }

        return player.world.getTileEntity(ray.getBlockPos());
    }

    private static void sendStatus(EntityPlayerMP player, String message, TextFormatting color) {
        final ITextComponent te = new TextComponentString(message);
        te.getStyle().setColor(color);
        player.sendStatusMessage(te, true);
    }
}
