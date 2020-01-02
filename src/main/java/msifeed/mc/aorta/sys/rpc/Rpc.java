package msifeed.mc.aorta.sys.rpc;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Rpc {
    private static final Rpc INSTANCE = new Rpc();

    private final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("aorta.rpc");
    private final HashMap<String, Handler> handlers = new HashMap<>();

    private Rpc() {}

    public static void init() {
        INSTANCE.CHANNEL.registerMessage(RpcMessage.class, RpcMessage.class, 0, Side.SERVER);
        INSTANCE.CHANNEL.registerMessage(RpcMessage.class, RpcMessage.class, 1, Side.CLIENT);
    }

    public static void register(Object obj) {
        for (Method m : obj.getClass().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(RpcMethod.class))
                continue;

            final Class<?>[] types = m.getParameterTypes();
            if (types.length == 0)
                throw new RuntimeException("RPC method missing MessageContext param");
            for (int i = 1; i < types.length; ++i)
                if (!types[i].isPrimitive() && !Serializable.class.isAssignableFrom(types[i]))
                    throw new RuntimeException("RPC method's param " + (i + 1) + "is not Serializable");

            final RpcMethod a = m.getAnnotation(RpcMethod.class);
            if (INSTANCE.handlers.containsKey(a.value()))
                throw new RuntimeException("RPC method duplication: `" + a.value() + "`");
            INSTANCE.handlers.put(a.value(), new Handler(obj, m));
        }
    }

    public static void sendToServer(String method, Serializable... args) {
        INSTANCE.CHANNEL.sendToServer(new RpcMessage(method, args));
    }

    public static void sendTo(EntityPlayerMP player, String method, Serializable... args) {
        INSTANCE.CHANNEL.sendTo(new RpcMessage(method, args), player);
    }

    public static void sendToAll(String method, Serializable... args) {
        INSTANCE.CHANNEL.sendToAll(new RpcMessage(method, args));
    }

    public static void sendToAllAround(String method, NetworkRegistry.TargetPoint point, Serializable... args) {
        INSTANCE.CHANNEL.sendToAllAround(new RpcMessage(method, args), point);
    }

    static void onMessage(RpcMessage message, MessageContext ctx) {
        final Handler h = INSTANCE.handlers.get(message.method);
        if (h == null)
            return;

        final Object[] args = new Object[message.args.length + 1];
        args[0] = ctx;
        System.arraycopy(message.args, 0, args, 1, message.args.length);

        final Class<?>[] expTypes = h.method.getParameterTypes();
        if (args.length != expTypes.length)
            return;
        for (int i = 0; i < expTypes.length; ++i)
            if (!isInstance(expTypes[i], args[i]))
                return;

        try {
            h.method.invoke(h.object, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isInstance(Class c, Object o) {
        if (c.isPrimitive()) {
            switch (c.getName()) {
                case "boolean":
                    return o instanceof Boolean;
                case "byte":
                    return o instanceof Byte;
                case "short":
                    return o instanceof Short;
                case "int":
                    return o instanceof Integer;
                case "long":
                    return o instanceof Long;
                case "float":
                    return o instanceof Float;
                case "double":
                    return o instanceof Double;
                default:
                    return false;
            }
        } else {
            return c.isInstance(o);
        }
    }

    private static class Handler {
        Object object;
        Method method;

        Handler(Object o, Method m) {
            this.object = o;
            this.method = m;
        }
    }
}
