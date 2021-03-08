package msifeed.misca.cmd;

import msifeed.misca.MiscaPerms;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleManager;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayer && MiscaPerms.userLevel(sender, "misca.combat");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        final EntityPlayer player = (EntityPlayer) sender;
        final Battle battle = Combat.MANAGER.getEntityBattle(player);

        if (args.length == 1) {
            if (battle == null)
                return getListOfStringsMatchingLastWord(args, "init");
            if (!battle.isStarted())
                return getListOfStringsMatchingLastWord(args, "start", "add", "leave", "destroy");
            if (MiscaPerms.isGameMaster(sender))
                return getListOfStringsMatchingLastWord(args, "next", "pup", "pos", "add", "leave", "destroy");
            else
                return getListOfStringsMatchingLastWord(args, "next", "pos", "add", "leave", "destroy");
        } else if (args.length == 2) {
            if (battle == null)
                return Collections.emptyList();

            if (args[0].equals("pup") && MiscaPerms.isGameMaster(sender)) {
                final List<Integer> pups = battle.getCombatants()
                        .map(Entity::getEntityId)
                        .collect(Collectors.toList());
                pups.add(0);
                return getListOfStringsMatchingLastWord(args, "next", "pos", "add", "leave", "destroy");
            } else {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        final EntityPlayerMP player = (EntityPlayerMP) sender;

        if (args.length == 0) {
            final Battle battle = Combat.MANAGER.getEntityBattle(player);
            if (battle == null) return;

            final List<String> queue = battle.getQueue().stream()
                    .map(uuid -> uuidToEntity(player.world, uuid))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Entity::getName)
                    .collect(Collectors.toList());
            final List<String> members = battle.getMembers().entrySet().stream()
                    .map(CombatCommand::getEntityName)
                    .collect(Collectors.toList());
            final String leader = uuidToEntity(player.world, battle.getLeaderUuid()).map(Entity::getName).orElse("dunno");
            player.sendStatusMessage(new TextComponentString("training: " + battle.isTraining()), false);
            player.sendStatusMessage(new TextComponentString("leader: " + leader), false);
            player.sendStatusMessage(new TextComponentString("queue: " + joinNiceStringFromCollection(queue)), false);
            player.sendStatusMessage(new TextComponentString("members: " + joinNiceStringFromCollection(members)), false);

            return;
        }

        final BattleManager manager = Combat.MANAGER;
        final ICombatant com = CombatantProvider.get(player);

        switch (args[0]) {
            default:
                player.sendStatusMessage(new TextComponentString("unknown command"), true);
                break;
            case "i":
            case "init":
                manager.initBattle(player, args.length > 1);
                player.sendStatusMessage(new TextComponentString("init"), true);
                break;
            case "s":
            case "start":
                manager.startBattle(player);
                player.sendStatusMessage(new TextComponentString("start"), true);
                break;
            case "l":
            case "leave":
                manager.leaveFromBattle(player);
                player.sendStatusMessage(new TextComponentString("leave"), true);
                break;
            case "q":
                // quick start for testing
                manager.initBattle(player, args.length > 1);
            case "a":
            case "add":
                if (!com.isInBattle()) break;
                player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(2)).stream()
                        .filter(e -> e instanceof EntityLivingBase)
                        .map(e -> (EntityLivingBase) e)
                        .forEach(e -> {
                            manager.addToBattle(com.getBattleId(), e);
                            player.sendStatusMessage(new TextComponentString("combat add " + e.getName()), false);
                        });

                if (args[0].equals("q")) {
                    manager.startBattle(player);
                }
                break;
            case "next": {
                if (!com.isInBattle()) break;
                manager.nextTurn(manager.getBattle(com.getBattleId()));
                player.sendStatusMessage(new TextComponentString("next"), true);
                break;
            }
            case "pup": {
                if (!com.isInBattle()) break;
                final Battle battle = manager.getBattle(com.getBattleId());
                if (battle == null) break;

                if (args.length >= 2) {
                    final int id = parseInt(args[1]);
                    if (player.getEntityId() == id) {
                        player.sendStatusMessage(new TextComponentString("Puppet cannot be you"), true);
                        break;
                    }

                    final EntityLivingBase pup = battle.getCombatants()
                            .filter(e -> e.getEntityId() == id)
                            .findAny().orElse(null);
                    if (pup != null) {
                        com.setPuppet(id);
                        player.sendStatusMessage(new TextComponentString("Puppet = " + pup.getName()), true);
                    } else {
                        player.sendStatusMessage(new TextComponentString("Puppet not found"), true);
                        break;
                    }
                } else {
                    com.setPuppet(0);
                    player.sendStatusMessage(new TextComponentString("Puppet disabled"), true);
                }

                CombatantSync.sync(player);
                break;
            }
            case "pos":
                manager.repositionMembers(player);
                break;
            case "destroy":
                if (!com.isInBattle()) break;
                manager.destroyBattle(manager.getBattle(com.getBattleId()));
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

    private static String getEntityName(Map.Entry<UUID, WeakReference<EntityLivingBase>> entry) {
        final EntityLivingBase entity = entry.getValue().get();
        if (entity != null)
            return entity.getName();
        else
            return entry.getKey().toString().substring(0, 6);
    }
}
