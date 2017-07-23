package ru.ariadna.misca.channels;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import ru.ariadna.misca.channels.ChannelsException.Type;
import ru.ariadna.misca.database.DBHandler;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ChannelManager {
    private Pattern channelNamePattern = Pattern.compile("^[0-9a-zA-Z]{2,20}$");
    private ChannelProvider provider;
    private Multimap<String, EntityPlayer> overhearOps = HashMultimap.create();

    ChannelManager(ChannelProvider provider) {
        this.provider = provider;
    }

    private static boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

    Collection<String> listPlayerChannels(String username) {
        String lower = username.toLowerCase();
        return provider.getChannels().values().stream()
                .filter(ch -> ch.players.contains(lower))
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
        if (sender instanceof EntityPlayer) {
            ch.players.add(sender.getCommandSenderName().toLowerCase());
        }
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

        ch.players.add(sender.getCommandSenderName().toLowerCase());
        provider.updateChannel(ch);

        sender.addChatMessage(new ChatComponentTranslation("misca.channels.join", channel));
    }

    void inviteToChannel(ICommandSender sender, String channel, String username) throws ChannelsException {
        Channel ch = provider.getChannel(channel);

        if (!ch.canInvite && isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }

        ch.players.add(username.toLowerCase());
        provider.updateChannel(ch);

        sender.addChatMessage(new ChatComponentTranslation("misca.channels.invite", username, channel));
    }

    void leaveChannel(ICommandSender sender, String channel) throws ChannelsException {
        if (!(sender instanceof EntityPlayer)) {
            throw new ChannelsException(Type.PLAYER_ONLY);
        }

        Channel ch = provider.getChannel(channel);
        ch.players.remove(sender.getCommandSenderName().toLowerCase());
        provider.updateChannel(ch);

        sender.addChatMessage(new ChatComponentTranslation("misca.channels.leave", channel));
    }

    void excludeFromChannel(ICommandSender sender, String channel, String player) throws ChannelsException {
        if (isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }

        Channel ch = provider.getChannel(channel);
        ch.players.remove(player.toLowerCase());
        provider.updateChannel(ch);

        sender.addChatMessage(new ChatComponentTranslation("misca.channels.exclude", player, channel));
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

    void infoChannel(ICommandSender sender, String channel) throws ChannelsException {
        Channel ch = provider.getChannel(channel);
        if (isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }

        List<String> lines = Arrays.asList(
                String.format("[%s]", ch.name),
                String.format(" radius: %d", ch.radius),
                String.format(" link: %b", ch.isLink),
                String.format(" global: %b", ch.isGlobal),
                String.format(" muted: %b", ch.isMuted),
                String.format(" public: %b", ch.isPublic),
                String.format(" can invite: %b", ch.canInvite),
                String.format(" players: %d", ch.players.size())
        );

        for (String line : lines) {
            sender.addChatMessage(new ChatComponentText(line));
        }
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
                if (isBoolean(value)) {
                    ch.isMuted = Boolean.valueOf(value);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.muted", ch.canInvite);
                } else {
                    response = new ChatComponentTranslation("misca.channels.set.true_or_false");
                    error = true;
                }
                break;
            case "invites":
                if (isBoolean(value)) {
                    ch.canInvite = Boolean.valueOf(value);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.invites", ch.canInvite);
                } else {
                    response = new ChatComponentTranslation("misca.channels.set.true_or_false");
                    error = true;
                }
                break;
            case "public":
                if (isBoolean(value)) {
                    ch.isPublic = Boolean.valueOf(value);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.public", ch.isPublic);
                } else {
                    response = new ChatComponentTranslation("misca.channels.set.true_or_false");
                    error = true;
                }
                break;
            case "global":
                if (isBoolean(value)) {
                    ch.isGlobal = Boolean.valueOf(value);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.global", ch.isGlobal);
                } else {
                    response = new ChatComponentTranslation("misca.channels.set.true_or_false");
                    error = true;
                }
                break;
            case "link":
                if (isBoolean(value)) {
                    ch.isLink = Boolean.valueOf(value);
                    provider.updateChannel(ch);
                    response = new ChatComponentTranslation("misca.channels.set.link", ch.isLink);
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

    void overhearChannel(ICommandSender sender, String channel) throws ChannelsException {
        if (!(sender instanceof EntityPlayer)) {
            throw new ChannelsException(Type.PLAYER_ONLY);
        }
        Channel ch = provider.getChannel(channel);
        if (isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }

        EntityPlayer player = (EntityPlayer) sender;
        if (overhearOps.containsEntry(ch.name, player)) {
            overhearOps.remove(ch.name, player);
            sender.addChatMessage(new ChatComponentTranslation("misca.channels.overhear.off", channel));
        } else {
            overhearOps.put(ch.name, player);
            sender.addChatMessage(new ChatComponentTranslation("misca.channels.overhear.on", channel));
        }
    }

    void sendToChannel(ICommandSender sender, String channel, String text) throws ChannelsException {
        Channel ch = provider.getChannel(channel);
        if (ch.isLink) {
            sender.addChatMessage(new ChatComponentTranslation("misca.channels.error.not_channel"));
            return;
        }

        sendToSomeone(sender, ch, null, text);
    }

    void sendToLink(ICommandSender sender, String channel, String player, String text) throws ChannelsException {
        Channel ch = provider.getChannel(channel);
        if (!ch.isLink) {
            sender.addChatMessage(new ChatComponentTranslation("misca.channels.error.not_link"));
            return;
        }

        sendToSomeone(sender, ch, player.toLowerCase(), text);
    }

    private void sendToSomeone(ICommandSender sender, Channel ch, String player, String text) throws ChannelsException {
        if (ch.isMuted && isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }

        Collection<String> ch_player = ch.players;
        if (!(sender instanceof MinecraftServer) && !ch_player.contains(sender.getCommandSenderName().toLowerCase())) {
            sender.addChatMessage(new ChatComponentTranslation("misca.channels.error.not_member"));
            return;
        }
        if (ch.isLink && !ch_player.contains(player)) {
            sender.addChatMessage(new ChatComponentTranslation("misca.channels.error.player_not_found"));
            return;
        }

        ServerConfigurationManager scm = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
        Stream<EntityPlayer> pl_stream = scm.playerEntityList.stream();

        if (ch.isLink) {
            pl_stream = pl_stream.filter(pl -> pl.getDisplayName().toLowerCase().equals(player));
        }

        if (!ch.isGlobal) {
            pl_stream = pl_stream.filter(pl -> ch_player.contains(pl.getDisplayName().toLowerCase()));
        }

        if ((sender instanceof EntityPlayer) && ch.radius > 0) {
            ChunkCoordinates coord = sender.getPlayerCoordinates();
            pl_stream = pl_stream.filter(pl -> pl.getPlayerCoordinates().getDistanceSquaredToChunkCoordinates(coord) < ch.radius);
        }

        String message = String.format(ch.format, ch.name, sender.getCommandSenderName(), text);
        ChatComponentText message_cmp = new ChatComponentText(message);
        Set<EntityPlayer> filtered_players = pl_stream.collect(Collectors.toSet());

        if (ch.isLink && filtered_players.isEmpty()) {
            sender.addChatMessage(new ChatComponentTranslation("misca.channels.error.player_not_found"));
            return;
        }

        if (ch.isLink && (sender instanceof EntityPlayer)) {
            filtered_players.add((EntityPlayer) sender);
        }

        for (EntityPlayer pl : filtered_players) {
            pl.addChatMessage(message_cmp);
        }

        // Overhearing ops ^_^
        if (ch.isLink) {
            EntityPlayer receiver = filtered_players.iterator().next();
            String overhear_msg = String.format(
                    "[OH][%s]%s-%s: %s",
                    ch.name, sender.getCommandSenderName(), receiver.getDisplayName(), text);
            ChatComponentText overhear_cmp = new ChatComponentText(overhear_msg);
            for (EntityPlayer op : overhearOps.get(ch.name)) {
                op.addChatComponentMessage(overhear_cmp);
            }
        }

        ChatChannels.logger.info(message);
        DBHandler.INSTANCE.logMessage(sender, player == null ? "cc" : "cl", text);
    }

    void reloadChannels(ICommandSender sender) throws ChannelsException {
        if (isSenderNotSuperuser(sender)) {
            throw new ChannelsException(Type.NO_PERM);
        }
        provider.reloadConfigFile();
    }

    boolean isSenderNotSuperuser(ICommandSender sender) {
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
