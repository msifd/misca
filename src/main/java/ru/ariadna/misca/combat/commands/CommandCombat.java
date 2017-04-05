package ru.ariadna.misca.combat.commands;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import ru.ariadna.misca.combat.Combat;
import ru.ariadna.misca.combat.CombatManager;
import ru.ariadna.misca.combat.fight.Fighter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandCombat implements ICommand {
    private static final List<String> other_cmd = Arrays.asList("help", "init");
    private static final List<String> attack_cmd = Arrays.asList("help", "hit", "shoot", "magic", "slam", "other", "special", "safe", "flee");
    private static final List<String> defence_cmd = Arrays.asList("help", "defence", "dodge", "magic", "dum", "stop");
    private CombatManager manager;

    public CommandCombat(CombatManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommandName() {
        return "cmb";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cmb <cmd> [...] ; /cmb help";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0 && args[0].equalsIgnoreCase("help")) {
            sendHelp(sender, "help");
            return;
        }


    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        Fighter f = manager.getFighter(sender.getCommandSenderName());
        if (f == null) {
            return other_cmd;
        }
        if (args.length > 1) {
            return null;
        }

        if (f.isAttacking) {
            return attack_cmd.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        } else {
            return defence_cmd.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
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
