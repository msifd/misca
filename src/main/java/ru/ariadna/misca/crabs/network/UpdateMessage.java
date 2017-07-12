package ru.ariadna.misca.crabs.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import ru.ariadna.misca.crabs.lobby.Lobby;

public class UpdateMessage<T> implements IMessage {
    public transient Lobby lobby;
    private transient byte[] cache;

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}
