package msifeed.sys.rpc;

import com.google.common.annotations.Beta;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Method;
import java.util.EnumMap;

public class RpcChannel {
    private final RpcHandlerRegistry registry;
    private final EnumMap<Side, FMLEmbeddedChannel> channels;

    public RpcChannel(String channel) {
        this(channel, new RpcCodec());
    }

    public RpcChannel(String channel, RpcCodec codec) {
        this.registry = new RpcHandlerRegistry(codec);
        this.channels = NetworkRegistry.INSTANCE.newChannel(channel, new RpcPacketCodec(registry, codec));

        channels.get(Side.SERVER).pipeline().addLast(new RpcChannelHandler(registry, Side.SERVER));
        channels.get(Side.CLIENT).pipeline().addLast(new RpcChannelHandler(registry, Side.CLIENT));
    }

    public void register(Object obj) {
        this.registry.register(obj);
    }

    public void register(String rpcMethod, Object obj, Method m) {
        this.registry.register(rpcMethod, obj, m);
    }

    public void sendToAll(String method, Object... args) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(new RpcMessage(method, args)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendTo(EntityPlayerMP player, String method, Object... args) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(new RpcMessage(method, args)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToAllAround(Entity entity, int range, String method, Object... args) {
        final NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, range);
        sendToAllAround(point, method, args);
    }

    public void sendToAllAround(NetworkRegistry.TargetPoint point, String method, Object... args) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(Side.SERVER).writeAndFlush(new RpcMessage(method, args)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToAllVisibleTo(Entity entity, String method, Object... args) {
        final NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 0);
        sendToAllTracking(point, method, args);
    }

    public void sendToAllTracking(NetworkRegistry.TargetPoint point, String method, Object... args) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TRACKING_POINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(Side.SERVER).writeAndFlush(new RpcMessage(method, args)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Will not work as expected
     */
    public void sendToAllTracking(Entity entity, String method, Object... args) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TRACKING_ENTITY);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(entity);
        channels.get(Side.SERVER).writeAndFlush(new RpcMessage(method, args)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToDimension(int dimensionId, String method, Object... args) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
        channels.get(Side.SERVER).writeAndFlush(new RpcMessage(method, args)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToServer(String method, Object... args) {
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(new RpcMessage(method, args)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
