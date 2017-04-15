package ru.ariadna.misca.combat.commands;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import ru.ariadna.misca.combat.Combat;
import ru.ariadna.misca.combat.CombatException;
import ru.ariadna.misca.combat.fight.FightManager;
import ru.ariadna.misca.combat.characters.CharacterProvider;
import ru.ariadna.misca.combat.fight.Action;
import ru.ariadna.misca.combat.fight.Fighter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandCombat implements ICommand {
    private static final List<String> attack_cmd;
    private static final List<String> defence_cmd;
    private static final Set<String> all_cmd;

    static {
        attack_cmd = Arrays.stream(Action.values())
                .filter(a -> a.stage == Action.Stage.ATTACK || a.stage == Action.Stage.FIGHT)
                .map(Action::toString).collect(Collectors.toList());
        defence_cmd = Arrays.stream(Action.values())
                .filter(a -> a.stage == Action.Stage.DEFENCE || a.stage == Action.Stage.FIGHT)
                .map(Action::toString).collect(Collectors.toList());
        all_cmd = Stream.of(attack_cmd, defence_cmd)
                .flatMap(Collection::stream).collect(Collectors.toSet());
        attack_cmd.add("help");
        defence_cmd.add("help");
    }

    private final CharacterProvider provider;
    private final FightManager manager;

    public CommandCombat(CharacterProvider provider, FightManager manager) {
        this.provider = provider;
        this.manager = manager;
    }

    @Override
    public String getCommandName() {
        return "cmb";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cmb <cmd> [...] ; /cmb help [cmd]";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender, "help");
            return;
        }
        if (args[0].equalsIgnoreCase("help")) {
            if (args.length >= 2 && all_cmd.contains(args[1].toLowerCase())) {
                sendHelp(sender, args[1].toLowerCase());
            } else {
                sendHelp(sender, "help");
            }
            return;
        }

        String arg1 = args[0];

        int modifier = 0;
        if (args.length >= 2 && (args[1].startsWith("+") || args[1].startsWith("-"))) {
            try {
                modifier = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                // ignore?
            }
        }

        Action action = Action.valueOf(arg1.toUpperCase());
        try {
            manager.doAction((EntityPlayer) sender, action, modifier);
        } catch (CombatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
//        return sender instanceof EntityPlayer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        Fighter f = manager.getFighter(sender.getCommandSenderName());
        if (args.length > 1) {
            return null;
        }

        switch (f.stage) {
            case ATTACK:
                return attack_cmd.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
            case DEFENCE:
                return defence_cmd.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
            default:
                return null;
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object cmp) {
        return this.getCommandName().compareTo(((ICommand) cmp).getCommandName());
    }

    private void sendHelp(ICommandSender sender, String postfix) {
        String msg = LanguageRegistry.instance().getStringLocalization("misca.combat.cmb.help." + postfix);
        msg = StringEscapeUtils.unescapeJava(msg);
        try {
            for (String line : IOUtils.readLines(new StringReader(msg))) {
                sender.addChatMessage(new ChatComponentText(line));
            }
        } catch (IOException e) {
            Combat.logger.error("Unreachable! Tried to read combat help by line.");
        }
    }
}
