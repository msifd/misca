package msifeed.misca.cmd;

import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.CharSkill;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.sys.cap.IntContainer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkillsCommand extends CommandBase {
    private static final Set<String> skillNames = Stream.of(CharSkill.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    @Override
    public String getName() {
        return "skills";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/skills <who> <skill> [<value>]";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            case 2:
                return getListOfStringsMatchingLastWord(args, skillNames);
            case 3:
                return getListOfStringsMatchingLastWord(args,"add", "set");
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) throw new SyntaxErrorException("Expected at least 2 args");

        final EntityPlayerMP player = getPlayer(server, sender, args[0]);
        final CharSkill skill = getSkill(args[1]);

        final ICharsheet sheet = CharsheetProvider.get(player);
        final IntContainer<CharSkill> skills = sheet.skills();

        if (args.length >= 3 && MiscaPerms.isGameMaster(sender)) {
            final int value = parseInt(args[2], 0, 3);
            skills.set(skill, MathHelper.clamp(value, 0, 3));
        }

        final String rep = String.format("%s's %s: %d", player.getDisplayNameString(), skill.toString(), skills.get(skill));
        sender.sendMessage(new TextComponentString(rep));
    }

    private static CharSkill getSkill(String name) throws CommandException {
        try {
            return CharSkill.valueOf(name);
        } catch (Exception e) {
            throw new SyntaxErrorException("Invalid effort name");
        }
    }
}
