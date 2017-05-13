package ru.ariadna.misca.toolbox;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.BiomeDictionary;

import java.lang.reflect.Field;
import java.util.List;

public class CommandBiomeEnchantIds implements ICommand {
    @Override
    public String getCommandName() {
        return "misca-tools-ids";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText(generateBiomeReport()));
        sender.addChatMessage(new ChatComponentText(generateEnchantReport()));
    }

    private String generateBiomeReport() {
        try {
            Field biomeListField = BiomeDictionary.class.getDeclaredField("biomeList");
            biomeListField.setAccessible(true);
            Object[] biomeArray = (Object[]) biomeListField.get(null);
            return "Free biome ids: " + freeRanges(biomeArray);
        } catch (Exception e) {
            Toolbox.logger.error("Failed to get biome list!");
        }
        return "Free biome ids: unknown :(";
    }

    private String generateEnchantReport() {
        return "Free enchant ids: " + freeRanges(Enchantment.enchantmentsList);
    }

    private String freeRanges(Object[] arr) {
        StringBuilder sb = new StringBuilder();

        boolean block = false;
        int first = 0;
        int last = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) {
                if (!block) {
                    first = i;
                }
                last = i;
                block = true;
            } else if (block) {
                block = false;
                sb.append(", ");
                sb.append(first);
                if (first != last) {
                    sb.append("-");
                    sb.append(last);
                }
            }
        }

        if (arr[arr.length - 1] == null) {
            sb.append(", ");
            sb.append(first);
            if (first != last) {
                sb.append("-");
                sb.append(last);
            }
        }

        String res = sb.toString();
        if (res.length() > 2) res = res.substring(2);
        return res;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (sender instanceof MinecraftServer) {
            return true;
        } else if (sender instanceof EntityPlayer) {
            // Is op?
            ServerConfigurationManager scm = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
            EntityPlayer pl = (EntityPlayer) sender;
            if (scm.func_152596_g(pl.getGameProfile())) {
                return true;
            }
        }
        return false;
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

}
