package msifeed.sys.rpc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.Reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RpcChannel implements IMessageHandler<RpcMessage, IMessage> {
    private final SimpleNetworkWrapper channel;
    private final HashMap<String, Handler> handlers = new HashMap<>();

    private final Logger logger;

    public RpcChannel(String channel) {
        this.channel = NetworkRegistry.INSTANCE.newSimpleChannel(channel);
        this.channel.registerMessage(this, RpcMessage.class, 0, Side.CLIENT);
        this.channel.registerMessage(this, RpcMessage.class, 1, Side.SERVER);

        logger = LogManager.getLogger("RPC:" + channel);
    }

    public void sendToAll(String method, Object... args) {
        channel.sendToAll(new RpcMessage(method, args));
    }

    public void sendTo(EntityPlayerMP player, String method, Object... args) {
        channel.sendTo(new RpcMessage(method, args), player);
    }

    public void sendToAllAround(NetworkRegistry.TargetPoint point, String method, Object... args) {
        channel.sendToAllAround(new RpcMessage(method, args), point);
    }

    public void sendToAllTracking(NetworkRegistry.TargetPoint point, String method, Object... args) {
        channel.sendToAllTracking(new RpcMessage(method, args), point);
    }

    public void sendToAllTracking(Entity entity, String method, Object... args) {
        channel.sendToAllTracking(new RpcMessage(method, args), entity);
    }

    public void sendToDimension(int dimensionId, String method, Object... args) {
        channel.sendToDimension(new RpcMessage(method, args), dimensionId);
    }

    public void sendToServer(String method, Object... args) {
        channel.sendToServer(new RpcMessage(method, args));
    }

    public void register(Object obj) {
        final Class<?> clazz = obj.getClass();
        for (Method m : clazz.getDeclaredMethods()) {
            if (!m.isAnnotationPresent(RpcMethodHandler.class))
                continue;
            final String methodName = m.getAnnotation(RpcMethodHandler.class).value();

            if (!m.isAccessible() && !Reflection.quickCheckMemberAccess(clazz, m.getModifiers()))
                throw new RuntimeException(String.format("RPC method '%s::%s' is not accessible", clazz.getName(), m.getName()));

            final Class<?>[] types = m.getParameterTypes();
            if (types.length == 0 || types[0] != MessageContext.class)
                throw new RuntimeException(String.format("RPC method '%s': missing leading MessageContext arg", methodName));

            for (int i = 1; i < types.length; ++i)
                if (!RpcCodec.INSTANCE.hasCodec(types[i]))
                    throw new RuntimeException(String.format("RPC method '%s': param %d (%s) is not supported", methodName, i + 1, types[i].getSimpleName()));

            if (handlers.containsKey(methodName))
                throw new RuntimeException(String.format("RPC method '%s': method duplication", methodName));
            handlers.put(methodName, new Handler(obj, m));
        }
    }

    @Override
    public IMessage onMessage(RpcMessage message, MessageContext ctx) {
        final Handler handler = handlers.get(message.method);
        if (handler == null)
            return null;

        final Object[] args = new Object[message.args.length + 1];
        args[0] = ctx;
        System.arraycopy(message.args, 0, args, 1, message.args.length);

        handler.invoke(args);
        return null;
    }

    private class Handler {
        Object object;
        Method method;

        Handler(Object o, Method m) {
            this.object = o;
            this.method = m;
        }

        void invoke(Object[] args) {
            try {
                method.invoke(object, args);
            } catch (IllegalArgumentException e) {
                final String expected = Stream.of(args)
                        .map(o -> o.getClass().getSimpleName())
                        .collect(Collectors.joining(","));
                final String actual = Stream.of(method.getParameterTypes())
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(","));
                logger.error("Method '{}' called with invalid arguments. Expected types: '{}', actual types: '{}'", method, expected, actual);
            } catch (RpcException e) {
                logger.error("Method '{}' failed with rpc error: {}", method, e);
                e.send();
            } catch (Exception e) {
                logger.error("Method '{}' failed with exception: {}", method, e);
            }
        }
    }
}
