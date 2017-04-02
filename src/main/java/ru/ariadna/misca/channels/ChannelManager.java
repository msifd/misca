package ru.ariadna.misca.channels;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import ru.ariadna.misca.channels.ChannelsException.Type;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ChannelManager {
    private Pattern channelNamePattern = Pattern.compile("^[0-9a-zA-Zа-яА-Я]{2,20}$");
    private ChannelProvider provider;

    ChannelManager(ChannelProvider provider) {
        this.provider = provider;
    }

    Collection<String> listPlayerChannels(String username) {
        return provider.getChannels().values().stream()
                .filter(ch -> ch.players.contains(username))
                .map(ch -> ch.name)
                .collect(Collectors.toList());
    }

    void registerChannel(ICommandSender sender, String channel) throws ChannelsException {
        if (isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }
        if (!channelNamePattern.matcher(channel).matches()) {
            sender.addChatMessage(new ChatComponentTranslation("misca.channels.register.format"));
            return;
        }
        if (provider.channelExist(channel)) {
            sender.addChatMessage(new ChatComponentTranslation("misca.channels.register.already_exists"));
            return;
        }

        Channel ch = new Channel(channel);
        ch.players.add(sender.getCommandSenderName());
        provider.updateChannel(ch);

        sender.addChatMessage(new ChatComponentTranslation("misca.channels.register", channel));
    }

    void joinToChannel(ICommandSender sender, String channel) throws ChannelsException {
        if (!(sender instanceof EntityPlayer)) {
            throw new ChannelsException(Type.PLAYER_ONLY);
        }
        Channel ch = provider.getChannel(channel);
        if (!ch.isPublic && isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }

        String username = sender.getCommandSenderName();
        ch.players.add(username);
        provider.updateChannel(ch);

        sender.addChatMessage(new ChatComponentTranslation("misca.channels.join", channel));
    }

    void inviteToChannel(ICommandSender sender, String channel, String username) throws ChannelsException {
        Channel ch = provider.getChannel(channel);

        if (!ch.canInvite && isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }

        ch.players.add(username);
        provider.updateChannel(ch);

        sender.addChatMessage(new ChatComponentTranslation("misca.channels.invite", username, channel));
    }

    void leaveChannel(ICommandSender sender, String channel) throws ChannelsException {
        if (!(sender instanceof EntityPlayer)) {
            throw new ChannelsException(Type.PLAYER_ONLY);
        }

        Channel ch = provider.getChannel(channel);
        String username = sender.getCommandSenderName();
        ch.players.remove(username);
        provider.updateChannel(ch);

        sender.addChatMessage(new ChatComponentTranslation("misca.channels.leave", channel));
    }

    void removeChannel(ICommandSender sender, String channel) throws ChannelsException {
        Channel ch = provider.getChannel(channel);
        if (isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }

        ch.players.clear();
        provider.removeChannel(ch);

        sender.addChatMessage(new ChatComponentTranslation("misca.channels.remove", channel));
    }

    void modifyChannel(ICommandSender sender, String channel, String param, String value) throws ChannelsException {
        Channel ch = provider.getChannel(channel);
        if (isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }

        boolean error = false;
        ChatComponentTranslation response;
        switch (param.toLowerCase()) {
            case "name":
                if (provider.channelExist(value)) {
                    response = new ChatComponentTranslation("misca.channels.set.name.occupied", value);
                    error = true;
                } else if (!channelNamePattern.matcher(value).matches()) {
                    response = new ChatComponentTranslation("misca.channels.register.format");
                    error = true;
                } else {
                    ch.name = value;
                    provider.getChannels().remove(channel);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.name", channel, value);
                }
                break;
            case "radius":
                try {
                    ch.radius = Integer.parseUnsignedInt(value);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.radius", ch.radius);
                } catch (NumberFormatException e) {
                    response = new ChatComponentTranslation("misca.channels.set.radius.format");
                    error = true;
                }
                break;
            case "muted":
                if (Boolean.parseBoolean(value)) {
                    ch.isMuted = Boolean.valueOf(value);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.muted", ch.canInvite);
                } else {
                    response = new ChatComponentTranslation("misca.channels.set.true_or_false");
                    error = true;
                }
                break;
            case "invites":
                if (Boolean.parseBoolean(value)) {
                    ch.canInvite = Boolean.valueOf(value);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.invites", ch.canInvite);
                } else {
                    response = new ChatComponentTranslation("misca.channels.set.true_or_false");
                    error = true;
                }
                break;
            case "public":
                if (Boolean.parseBoolean(value)) {
                    ch.isPublic = Boolean.valueOf(value);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.public", ch.isPublic);
                } else {
                    response = new ChatComponentTranslation("misca.channels.set.true_or_false");
                    error = true;
                }
                break;
            case "global":
                if (Boolean.parseBoolean(value)) {
                    ch.isGlobal = Boolean.valueOf(value);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.global", ch.isGlobal);
                } else {
                    response = new ChatComponentTranslation("misca.channels.set.true_or_false");
                    error = true;
                }
                break;
            default:
                response = new ChatComponentTranslation("misca.channels.set.unknown_param");
                break;
        }

        sender.addChatMessage(response);

        if (!error) provider.updateChannel(ch);
    }

    void sendToChannel(String channel, ICommandSender cmd_sender, String text) throws ChannelsException {
        Channel ch = provider.getChannel(channel);

        if (ch.isMuted && isSenderNotSuperuser(cmd_sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }

        Collection<String> ch_player = ch.players;
        if (!(cmd_sender instanceof MinecraftServer) && !ch_player.contains(cmd_sender.getCommandSenderName())) {
            throw new ChannelsException(Type.NOT_MEMBER);
        }

        final int radius_override;
        if ((cmd_sender instanceof EntityPlayer)) {
            radius_override = ch.radius;
        } else {
            radius_override = 0;
        }

        ServerConfigurationManager scm = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
        Stream<EntityPlayer> pl_stream = scm.playerEntityList.stream();

        if (!ch.isGlobal) {
            pl_stream = pl_stream.filter(pl -> ch_player.contains(pl.getDisplayName()));
        }

        if (radius_override > 0) {
            EntityPlayer sender = (EntityPlayer) cmd_sender;
            ChunkCoordinates coord = sender.getPlayerCoordinates();
            pl_stream = pl_stream.filter(pl -> pl.getPlayerCoordinates().getDistanceSquaredToChunkCoordinates(coord) < radius_override);
        }

        String message = String.format(ch.format, ch.name, cmd_sender.getCommandSenderName(), text);
        ChatComponentText message_cmp = new ChatComponentText(message);

        for (Iterator<EntityPlayer> it = pl_stream.iterator(); it.hasNext(); ) {
            it.next().addChatMessage(message_cmp);
        }
    }

    private boolean isSenderNotSuperuser(ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            ServerConfigurationManager scm = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
            EntityPlayer pl = (EntityPlayer) sender;
            if (scm.func_152596_g(pl.getGameProfile())) {
                return false;
            }
//            if (ch != null && ch.owner.equals(pl.getCommandSenderName())) {
//                return false;
//            }
        } else if (sender instanceof MinecraftServer) {
            return false;
        }
        return true;
    }
}
