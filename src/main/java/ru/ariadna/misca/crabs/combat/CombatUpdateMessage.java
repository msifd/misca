package ru.ariadna.misca.crabs.combat;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import ru.ariadna.misca.crabs.combat.parts.Move;
import ru.ariadna.misca.crabs.gui.GuiScreenCombat;
import ru.ariadna.misca.crabs.lobby.LobbyUpdateMessage;

import java.io.*;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class CombatUpdateMessage implements IMessage, IMessageHandler<CombatUpdateMessage, IMessage> {
    public transient Fight fight;

    private LobbyUpdateMessage lobbyMessage;
    private LinkedList<Integer> fighters;
    private LinkedList<Move> moves;
    private transient byte[] cache;

    public CombatUpdateMessage() {

    }

    public CombatUpdateMessage(Fight fight) {
        lobbyMessage = new LobbyUpdateMessage(fight.lobby);
        fighters = fight.queue.stream().map(Fighter::entity).map(Entity::getEntityId).collect(Collectors.toCollection(LinkedList::new));
        moves = fight.moves;
    }

    public void fromBytes(ByteBuf buf) {
        lobbyMessage = new LobbyUpdateMessage();
        lobbyMessage.fromBytes(buf);

        if (lobbyMessage.lobby == null) return;

        try {
            int size = buf.readInt();
            ByteBuf map_buf = buf.readBytes(size);

            ByteArrayInputStream bis = new ByteArrayInputStream(map_buf.array());
            ObjectInputStream ois = new ObjectInputStream(bis);

            fighters = (LinkedList<Integer>) ois.readObject();
            moves = (LinkedList<Move>) ois.readObject();

            LinkedList<Fighter> members = lobbyMessage.lobby.members();
            LinkedList<Fighter> fightersEntities = fighters.stream()
                    .map(id -> members.stream().filter(f -> f.entityId() == id).findFirst().get())
                    .collect(Collectors.toCollection(LinkedList::new));

            fight = new Fight(lobbyMessage.lobby);
            fight.queue = fightersEntities;
            fight.moves = moves;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        lobbyMessage.toBytes(buf);

        if (cache == null) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);

                oos.writeObject(fighters);
                oos.writeObject(moves);

                cache = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        buf.writeInt(cache.length);
        buf.writeBytes(cache);
    }

    @Override
    public IMessage onMessage(CombatUpdateMessage message, MessageContext ctx) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiScreenCombat) {
            GuiScreenCombat screenCombat = (GuiScreenCombat) Minecraft.getMinecraft().currentScreen;
            screenCombat.onCombatUpdate(message);
        }
        return null;
    }
}
