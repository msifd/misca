package ru.ariadna.misca.crabs.combat;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ru.ariadna.misca.crabs.combat.parts.Action;
import ru.ariadna.misca.crabs.combat.parts.Move;
import ru.ariadna.misca.crabs.gui.GuiScreenCombat;
import ru.ariadna.misca.crabs.lobby.LobbyUpdateMessage;

import java.io.*;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class CombatUpdateMessage implements IMessage, IMessageHandler<CombatUpdateMessage, IMessage> {
    public Fight fight;
    private LobbyUpdateMessage lobbyMessage;
    private transient byte[] cache;

    public CombatUpdateMessage() {

    }

    public CombatUpdateMessage(Fight fight) {
        this.fight = fight;
        this.lobbyMessage = new LobbyUpdateMessage(fight.lobby);
    }

    public void fromBytes(ByteBuf buf) {
        lobbyMessage = new LobbyUpdateMessage();
        lobbyMessage.fromBytes(buf);

        if (lobbyMessage.lobby == null) return;

        fight = new Fight(lobbyMessage.lobby);

        try {
            int size = buf.readInt();
            ByteBuf map_buf = buf.readBytes(size);

            ByteArrayInputStream bis = new ByteArrayInputStream(map_buf.array());
            ObjectInputStream ois = new ObjectInputStream(bis);

            LinkedList<Integer> queue_ids = new LinkedList<>();
            int queue_size = ois.readInt();
            for (int i = 0; i < queue_size; i++) queue_ids.add(ois.readInt());

            LinkedList<Fighter> queue = queue_ids.stream()
                    .map(id -> fight.lobby().findFighter(id))
                    .collect(Collectors.toCollection(LinkedList::new));


            LinkedList<Move> moves = new LinkedList<>();
            int moves_size = ois.readInt();
            for (int i = 0; i < moves_size; i++) {
                Move m = new Move();
                m.attacker = fight.lobby().findFighter(ois.readInt());
                m.defender = fight.lobby().findFighter(ois.readInt());
                m.attack = (Action) ois.readObject();
                m.defence = (Action) ois.readObject();
                moves.add(m);
            }

            Move cm = new Move();
            cm.attacker = fight.lobby().findFighter(ois.readInt());
            cm.defender = fight.lobby().findFighter(ois.readInt());
            cm.attack = (Action) ois.readObject();
            cm.defence = (Action) ois.readObject();

            fight.queue = queue;
            fight.moves = moves;
            fight.current_move = cm;

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

                oos.writeInt(fight.queue().size());
                for (Fighter f : fight.queue()) oos.writeInt(f.entityId());

                oos.writeInt(fight.moves().size());
                for (Move m : fight.moves()) {
                    oos.writeInt(m.attacker.entityId());
                    oos.writeInt(m.defender.entityId());
                    oos.writeObject(m.attack);
                    oos.writeObject(m.defence);
                }

                oos.writeInt(fight.current_move.attacker.entityId());
                oos.writeInt(fight.current_move.defender == null ? 0 : fight.current_move.defender.entityId());
                oos.writeObject(fight.current_move.attack);
                oos.writeObject(fight.current_move.defence);

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
        GuiScreenCombat.instance.onFightUpdate(message.fight);
        return null;
    }
}
