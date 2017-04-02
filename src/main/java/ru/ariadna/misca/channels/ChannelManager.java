package ru.ariadna.misca.channels;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import ru.ariadna.misca.channels.ChannelsException.Type;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ChannelManager {
    private Map<String, Channel> channels = new HashMap<>();
    private Multimap<Channel, String> channelPlayers = HashMultimap.create();
//    private Multimap<String, Channel> playerChannels = HashMultimap.create();

    void init() {

    }

    private Channel getChannel(String channel) throws ChannelsException {
        Channel ch = channels.get(channel);
        if (ch == null) throw new ChannelsException(Type.UNKNOWN_CHANNEL);
        return ch;
    }

    Collection<Channel> listPlayerChannels(String username) {
        // FIXME? add separate inverted map
        return channelPlayers.entries().stream().filter(e -> e.getValue().contains(username)).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    void registerChannel(String owner, String channel) throws ChannelsException {
        if (channels.get(channel) != null) {
            throw new ChannelsException(Type.CHANNEL_ALREADY_EXISTS);
        }
        Channel ch = new Channel(owner, channel);
        channels.put(channel, ch);
        channelPlayers.put(ch, owner);
//        playerChannels.put(owner, ch);
    }

    void joinToChannel(String username, String channel) throws ChannelsException {
        Channel ch = getChannel(channel);
        channelPlayers.put(ch, username);
//        playerChannels.put(username, ch);
    }

    void leaveChannel(String username, String channel) throws ChannelsException {
        Channel ch = getChannel(channel);
        channelPlayers.remove(ch, username);
//        playerChannels.remove(username, ch);
    }

    void removeChannel(EntityPlayer sender, String channel) throws ChannelsException {
        Channel ch = getChannel(channel);
        ServerConfigurationManager scm = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();

        // owner or OP
        if (ch.owner.equals(sender.getDisplayName()) || scm.func_152596_g(sender.getGameProfile())) {
            channels.remove(ch.name);
            channelPlayers.removeAll(ch);
//            playerChannels.values().removeIf(pc -> pc.equals(ch));
        } else {
            throw new ChannelsException(Type.NO_PERM);
        }
    }

    void modifyChannel(EntityPlayer sender, String channel, String key, String value) throws ChannelsException {

    }

    void sendToChannel(String channel, String sender_name, String text) throws ChannelsException {
        Channel ch = getChannel(channel);
        ServerConfigurationManager scm = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();

        Collection<String> ch_player = channelPlayers.get(ch);
        if (!ch_player.contains(sender_name)) {
            throw new ChannelsException(Type.NOT_MEMBER);
        }

        List<EntityPlayerMP> players = scm.playerEntityList;
        EntityPlayerMP sender = players.stream().filter(pl -> pl.getDisplayName().equals(sender_name)).findFirst().get();

        Stream<EntityPlayerMP> pl_stream = players.stream();
        pl_stream = pl_stream.filter(pl -> ch_player.contains(pl.getDisplayName()));

        if (ch.radius > 0) {
            ChunkCoordinates coord = sender.getPlayerCoordinates();
            pl_stream = pl_stream.filter(pl -> pl.getPlayerCoordinates().getDistanceSquaredToChunkCoordinates(coord) < ch.radius);
        }

        String message = String.format(ch.format, ch.name, sender_name, text);
        ChatComponentText message_cmp = new ChatComponentText(message);

        for (Iterator<EntityPlayerMP> it = pl_stream.iterator(); it.hasNext(); ) {
            it.next().addChatMessage(message_cmp);
        }
    }
}
