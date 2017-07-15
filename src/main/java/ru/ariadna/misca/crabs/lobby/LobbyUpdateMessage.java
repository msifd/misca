package ru.ariadna.misca.crabs.lobby;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import ru.ariadna.misca.crabs.combat.Fighter;
import ru.ariadna.misca.crabs.combat.FightManager;
import ru.ariadna.misca.crabs.gui.GuiScreenLobby;

import java.io.*;
import java.util.LinkedList;

public class LobbyUpdateMessage implements IMessage, IMessageHandler<LobbyUpdateMessage, IMessage> {
    public transient Lobby lobby;

    private int masterEntityId;
    private LinkedList<Fighter> fighters;
    private transient byte[] cache;

    public LobbyUpdateMessage() {

    }

    public LobbyUpdateMessage(Lobby lobby) {
        if (lobby != null) {
            this.masterEntityId = lobby.master().getEntityId();
            this.fighters = lobby.members();
        } else {
            this.masterEntityId = 0;
            this.fighters = new LinkedList<>();
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            int size = buf.readInt();
            ByteBuf map_buf = buf.readBytes(size);

            ByteArrayInputStream bis = new ByteArrayInputStream(map_buf.array());
            ObjectInputStream ois = new ObjectInputStream(bis);

            masterEntityId = ois.readInt();
            fighters = (LinkedList<Fighter>) ois.readObject();

            if (masterEntityId == 0) return;

            Fighter master = FightManager.makeFighterClient(masterEntityId);
            WorldClient worldClient = Minecraft.getMinecraft().theWorld;
            for (Fighter f : fighters)
                f.findEntityInWorld(worldClient);

            lobby = new Lobby(master);
            lobby.setMembers(fighters);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (cache == null) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);

                oos.writeInt(masterEntityId);
                oos.writeObject(fighters);

                cache = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        buf.writeInt(cache.length);
        buf.writeBytes(cache);
    }

    @Override
    public IMessage onMessage(LobbyUpdateMessage message, MessageContext ctx) {
        GuiScreenLobby.instance.onLobbyUpdate(message.lobby);
//        if (Minecraft.getMinecraft().currentScreen instanceof GuiScreenLobby) {
//            GuiScreenLobby lobbyScreen = (GuiScreenLobby) Minecraft.getMinecraft().currentScreen;
//            lobbyScreen.onLobbyUpdate(message.lobby);
//        }
        return null;
    }
}
