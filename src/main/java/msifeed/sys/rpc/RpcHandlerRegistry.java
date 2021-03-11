package msifeed.sys.rpc;

import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.Reflection;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RpcHandlerRegistry {
    private static final Logger logger = LogManager.getLogger();

    private final HashMap<String, Handler> handlers = new HashMap<>();
    private final RpcCodec codec;

    RpcHandlerRegistry(RpcCodec codec) {
        this.codec = codec;
    }

    public void register(Object obj) {
        final Class<?> clazz = obj.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(RpcMethodHandler.class))
                continue;
            final String rpcMethod = method.getAnnotation(RpcMethodHandler.class).value();
            register(rpcMethod, obj, method);
        }
    }

    public void register(String rpcMethod, Object obj, Method m) {
        final Class<?> clazz = obj.getClass();
        if (!m.isAccessible() && !Reflection.quickCheckMemberAccess(clazz, m.getModifiers()))
            throw new RuntimeException(String.format("RPC method '%s::%s' is not accessible", clazz.getName(), m.getName()));

        final Class<?>[] types = m.getParameterTypes();

        final boolean hasContext = types.length > 0 && types[0] == RpcContext.class;
        final int argsStart = hasContext ? 1 : 0;
        for (int i = argsStart; i < types.length; ++i)
            if (!codec.hasCodecForType(types[i]))
                throw new RuntimeException(String.format("RPC method '%s': param %d (%s) is not supported", rpcMethod, i + 1, types[i].getSimpleName()));

        if (handlers.containsKey(rpcMethod))
            throw new RuntimeException(String.format("RPC method '%s': handler already exists", rpcMethod));

        handlers.put(rpcMethod, new Handler(obj, m, hasContext));
    }

    @Nullable
    Class<?>[] getMethodParams(String method) {
        final Handler handler = handlers.get(method);
        if (handler != null)
            return handler.params;
        else
            return null;
    }

    void invoke(RpcMessage message, INetHandler netHandler, Side side) {
        final Handler handler = handlers.get(message.method);
        if (handler == null) return;

        if (handler.hasContext)
            handler.invoke(message.args, new RpcContext(netHandler, side));
        else
            handler.invoke(message.args);
    }

    private static class Handler {
        final Object object;
        final Method method;
        final boolean hasContext;
        final Class<?>[] params;

        Handler(Object o, Method m, boolean hasContext) {
            this.object = o;
            this.method = m;
            this.hasContext = hasContext;
            this.params = Stream.of(method.getParameterTypes())
                .filter(c -> !c.equals(RpcContext.class))
                .toArray(Class<?>[]::new);
        }

        void invoke(Object[] args, RpcContext ctx) {
            final Object[] argsWithCtx = new Object[args.length + 1];
            argsWithCtx[0] = ctx;
            System.arraycopy(args, 0, argsWithCtx, 1, args.length);

            invoke(argsWithCtx);
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
                logger.error("Method '{}' failed with exception: {}", method, e.getCause());
                logger.throwing(e.getCause());
            }
        }
    }
}
