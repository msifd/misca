package msifeed.mc.misca.tweaks.mining;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class CommandMiningStamina extends CommandBase {
    private MiningNerf module;

    CommandMiningStamina(MiningNerf module) {
        this.module = module;
    }

    @Override
    public String getCommandName() {
        return "stamina";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/stamina BWA-HA-HA!";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        MiningNerf.Stamina stamina_obj = module.playerStamina.get(sender.getCommandSenderName().toLowerCase());
        if (stamina_obj == null) return;

        float stamina = stamina_obj.value;
        String msg = LanguageRegistry.instance().getStringLocalization("misca.mining_stamina.msg");
        String formatted = String.format(msg, (int) Math.floor(stamina));
        sender.addChatMessage(new ChatComponentText(formatted));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }
}
