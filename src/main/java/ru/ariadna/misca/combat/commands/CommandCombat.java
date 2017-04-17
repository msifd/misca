package ru.ariadna.misca.combat.commands;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.combat.CombatException;
import ru.ariadna.misca.combat.calculation.CalcRulesProvider;
import ru.ariadna.misca.combat.fight.Action;
import ru.ariadna.misca.combat.fight.FightManager;
import ru.ariadna.misca.combat.fight.Fighter;

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
        // Список команд
        attack_cmd = Arrays.stream(Action.values())
                .filter(Action::isAttack)
                .map(Action::toString).collect(Collectors.toList());
        defence_cmd = Arrays.stream(Action.values())
                .filter(Action::isDefence)
                .map(Action::toString).collect(Collectors.toList());
        all_cmd = Stream.of(attack_cmd, defence_cmd)
                .flatMap(Collection::stream).collect(Collectors.toSet());
        attack_cmd.add("help");
        defence_cmd.add("help");
    }

    private String cmd_help_attack;
    private String cmd_help_defence;
    private String cmd_help_all;
    private final FightManager manager;

    public CommandCombat(FightManager manager, CalcRulesProvider rules) {
        this.manager = manager;

//        // Кэш хелпа. пачиму бы и нет %)
//        LanguageRegistry reg = LanguageRegistry.instance();
//        cmd_help_attack = buildHelp(rules, attack_cmd, Action.Stage.ATTACK);
//        cmd_help_defence = buildHelp(rules, defence_cmd, Action.Stage.DEFENCE);
//        cmd_help_all = reg.getStringLocalization("misca.combat.cmb.help") + cmd_help_attack + cmd_help_defence;
    }

    private static String buildHelp(CalcRulesProvider rules, List<String> cmds, Action.Stage stage) {
        LanguageRegistry reg = LanguageRegistry.instance();
        String prefix = reg.getStringLocalization("misca.combat.cmb.help." + stage.name().toLowerCase());
        StringBuilder sb = new StringBuilder(prefix);
        for (String cmd : cmds) {
            String h = LanguageRegistry.instance().getStringLocalization("misca.combat.cmb.help." + cmd);
            if (h == null || h.isEmpty()) continue;
            sb.append(cmd);
            sb.append(" - ");
            sb.append(h);
            sb.append(" (");
            sb.append(rules.getRule(Action.valueOf(cmd), stage));
            sb.append(")\n");
        }
        return sb.toString();
    }

    @Override
    public String getCommandName() {
        return "cmb";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cmb <cmd> [mod] ; /cmb help [cmd]";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender, "all");
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        switch (args[0].toLowerCase()) {
            // Общий хелп и специальный для команд
            case "help":
                if (args.length >= 2 && all_cmd.contains(args[1].toLowerCase())) {
                    sendHelp(sender, args[1].toLowerCase());
                } else {
                    sendHelp(sender, "all");
                }
                return;
            // Выбор персонажа для атаки
            case "target":
                if (args.length > 1) {
                    manager.selectTarget(player, args[1]);
                    return;
                }
                break;
            case "pass":
                manager.passTurn(player);
                return;
//            case "object":
//                // TODO
//                return;
        }

        // Пробуем прочитать модификатор к действию
        int modifier = 0;
        if (args.length > 1) {
            try {
                modifier = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                // ignore?
            }
        }

        try {
            Action action = Action.valueOf(args[0].toUpperCase());
            // Фильтруем системные действия (INIT)
            if (action.isSystem()) {
                throw new IllegalArgumentException();
            }
            manager.doAction(player, action, modifier);
        } catch (CombatException e) {
            sender.addChatMessage(new ChatComponentText(e.toString()));
        } catch (IllegalArgumentException e) {
            sender.addChatMessage(new ChatComponentTranslation("misca.combat.cmb.error.no_action"));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length > 1) {
            return null;
        }

        Fighter fighter = manager.getFighter((EntityPlayer) sender);
        switch (fighter.getStage()) {
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
        switch (postfix) {
            case "attack":
                MiscaUtils.sendMultiline(sender, cmd_help_attack);
                break;
            case "defence":
                MiscaUtils.sendMultiline(sender, cmd_help_defence);
                break;
            default:
                if (all_cmd.contains(postfix)) {
                    String h = LanguageRegistry.instance().getStringLocalization("misca.combat.cmb.help." + postfix);
                    MiscaUtils.sendMultiline(sender, h);
                } else {
                    MiscaUtils.sendMultiline(sender, cmd_help_all);
                }
                break;
        }
    }
}
