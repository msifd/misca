package ru.ariadna.misca.combat.commands;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.combat.lobby.LobbyManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandCombatLobby implements ICommand {
    private static final List<String> commands = Arrays.asList("help", "new", "leave", "fight", "list", "join");

    private final LobbyManager manager;

    public CommandCombatLobby(LobbyManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommandName() {
        return "cmb-lobby";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cmb-lobby <help|new|leave|fight|list|join <player>>";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        switch (args[0].toLowerCase()) {
            case "new":
                manager.createLobby(player);
                return;
            case "leave":
                manager.leaveLobby(player);
                return;
            case "fight":
                manager.fightLobby(player);
                return;
            case "list":
                manager.listLobby(player);
                return;
            case "join":
                if (args.length > 1) {
                    manager.joinLobby(player, args[1]);
                    return;
                }
                break;
        }

        sendHelp(sender);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            return commands;
        } else if (args.length == 1) {
            return commands.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        if (index == 1 && args[0].equalsIgnoreCase("join")) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Object cmp) {
        return this.getCommandName().compareTo(((ICommand) cmp).getCommandName());
    }

    private void sendHelp(ICommandSender sender) {
        String text = LanguageRegistry.instance().getStringLocalization("misca.combat.lobby.help");
        MiscaUtils.sendMultiline(sender, text);
    }
}
