package msifeed.misca.combat;

import msifeed.misca.combat.battle.Battle;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CombatCommand extends CommandBase {
    @Override
    public String getName() {
        return "combat";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "send help!";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        final EntityPlayerMP player = (EntityPlayerMP) sender;

        if (args.length == 0) {
            Combat.MANAGER.getEntityBattle(player).ifPresent(battle -> {
                final List<String> queue = battle.getQueue().stream()
                        .map(uuid -> uuidToEntity(player.world, uuid))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(Entity::getName)
                        .collect(Collectors.toList());
                final List<String> members = battle.getMembers().stream()
                        .map(uuid -> uuidToEntity(player.world, uuid))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(Entity::getName)
                        .collect(Collectors.toList());
                final String leader = uuidToEntity(player.world, battle.getLeader()).map(Entity::getName).orElse("dunno");
                player.sendStatusMessage(new TextComponentString("training: " + battle.isTraining()), false);
                player.sendStatusMessage(new TextComponentString("phase: " + battle.getPhase()), false);
                player.sendStatusMessage(new TextComponentString("leader: " + leader), false);
                player.sendStatusMessage(new TextComponentString("queue: " + joinNiceStringFromCollection(queue)), false);
                player.sendStatusMessage(new TextComponentString("members: " + joinNiceStringFromCollection(members)), false);
            });
            return;
        }

        switch (args[0]) {
            default:
                player.sendStatusMessage(new TextComponentString("unknown command"), true);
                break;
            case "i":
            case "init":
                Combat.MANAGER.initBattle(player, args.length > 1);
                player.sendStatusMessage(new TextComponentString("init"), true);
                break;
            case "s":
            case "start":
                Combat.MANAGER.startBattle(player);
                player.sendStatusMessage(new TextComponentString("start"), true);
                break;
            case "l":
            case "leave":
                Combat.MANAGER.leaveFromBattle(player);
                player.sendStatusMessage(new TextComponentString("leave"), true);
                break;
            case "a":
            case "add": {
                for (Entity e : player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(2))) {
                    if (!(e instanceof EntityLivingBase)) continue;
                    Combat.MANAGER.addToBattle(player, (EntityLivingBase) e);
                    player.sendStatusMessage(new TextComponentString("combat add " + e.getName()), false);
                }
                break;
            }
            case "next":
                Combat.MANAGER.getEntityBattle(player)
                        .filter(Battle::isStarted)
                        .ifPresent(Battle::finishTurn);
                player.sendStatusMessage(new TextComponentString("next"), true);
                break;
            case "destroy":
                Combat.MANAGER.destroyBattle(player);
                player.sendStatusMessage(new TextComponentString("destroy"), true);
                break;
        }
    }

    private static Optional<EntityLivingBase> uuidToEntity(World w, UUID uuid) {
        return w.loadedEntityList.stream()
                .filter(e -> e instanceof EntityLivingBase)
                .filter(e -> e.getUniqueID().equals(uuid))
                .findAny()
                .map(e -> (EntityLivingBase) e);
    }
}
