package ru.ariadna.misca.combat.commands;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import ru.ariadna.misca.combat.characters.Character;
import ru.ariadna.misca.combat.characters.CharacterProvider;

import java.util.List;

public class CommandCharStats implements ICommand {
    private final CharacterProvider provider;

    public CommandCharStats(CharacterProvider provider) {
        this.provider = provider;
    }

    @Override
    public String getCommandName() {
        return "charstats";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return LanguageRegistry.instance().getStringLocalization("misca.combat.charstats.usage");
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 7) {
            sender.addChatMessage(new ChatComponentTranslation("misca.combat.charstats.usage"));
            return;
        }

        try {
            Character c = new Character();
            c.name = sender.getCommandSenderName().toLowerCase();
            c.strength = toInt(args[0]);
            c.perception = toInt(args[1]);
            c.reflexes = toInt(args[2]);
            c.endurance = toInt(args[3]);
            c.determination = toInt(args[4]);
            c.wisdom = toInt(args[5]);
            c.spirit = toInt(args[6]);

            provider.updateCharacter(c);
            sender.addChatMessage(new ChatComponentTranslation("misca.combat.charstats.success", c.stats()));
        } catch (Throwable e) {
            sender.addChatMessage(new ChatComponentTranslation("misca.combat.charstats.usage"));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object cmp) {
        return this.getCommandName().compareTo(((ICommand) cmp).getCommandName());
    }

    private int toInt(String s) throws Exception {
        int i = Integer.valueOf(s);
        if (i < 0 || i > 10) throw new Exception();
        return i;
    }
}
