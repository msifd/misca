package msifeed.mc.misca.crabs.context;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.crabs.action.ActionManager;
import msifeed.mc.misca.utils.AbstractMessage;
import msifeed.mc.misca.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class ContextMessage extends AbstractMessage<ContextMessage> {
    private ArrayList<Context> contexts = new ArrayList<>();

    public ContextMessage() {
    }

    public ContextMessage(Collection<Context> contexts) {
        this.contexts.addAll(contexts);
    }

    @Override
    @SideOnly(Side.CLIENT) // Чтобы серевер не валился из-за `mc.theWorld`
    public void fromBytes(ByteBuf buf) {
        final byte len = buf.readByte();

        // Поскольку контексты отправляются только клиентам, мы ищем энтити только в их текущем мире
        final Minecraft mc = Minecraft.getMinecraft();
        final int currentDim = mc.thePlayer.dimension;
        // Фиксит урон по игроку в сингле, т.к. энтити игрока из WorldClient игнорит входящий урон.
        final World world = mc.isSingleplayer()
                ? mc.getIntegratedServer().worldServerForDimension(currentDim)
                : mc.theWorld;

        final HashMap<UUID, EntityLivingBase> worldEntities = new HashMap<>();
        for (Object o : world.loadedEntityList)
            if (o instanceof EntityLivingBase)
                worldEntities.put(EntityUtils.getUuid((EntityLivingBase) o), (EntityLivingBase) o);

        for (int i = 0; i < len; i++) {
            final Context.Status status = Context.Status.values()[buf.readByte()];
            final long lastStatusChange = buf.readLong();

            final UUID uuid = UUID.fromString(readShortString(buf));
            final Context ctx = new Context(uuid, null);

            ctx.status = status;
            ctx.lastStatusChange = lastStatusChange;

            if (status == Context.Status.DELETE) {
                contexts.add(ctx);
            }

            ctx.entity = worldEntities.get(uuid);

            if (status.isFighting()) {
                final String puppetStr = readShortString(buf);
                ctx.puppet = puppetStr.isEmpty() ? null : UUID.fromString(puppetStr);
                final String actionStr = readShortString(buf);
                ctx.action = actionStr.isEmpty() ? null : ActionManager.INSTANCE.lookup(actionStr);
                ctx.modifier = buf.readInt();
                ctx.described = buf.readBoolean();
                final String targetStr = readShortString(buf);
                ctx.target = targetStr.isEmpty() ? null : UUID.fromString(targetStr);
                ctx.damageDealt = buf.readFloat();
                ctx.knockedOut = buf.readBoolean();
            }

            contexts.add(ctx);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(contexts.size());

        for (Context ctx : contexts) {
            buf.writeByte(ctx.status.ordinal());
            buf.writeLong(ctx.lastStatusChange);

            writeShortString(buf, ctx.uuid.toString());

            if (ctx.status.isFighting()) {
                writeShortString(buf, ctx.puppet == null ? "" : ctx.puppet.toString());
                writeShortString(buf, ctx.action == null ? "" : ctx.action.name);
                buf.writeInt(ctx.modifier);
                buf.writeBoolean(ctx.described);
                writeShortString(buf, ctx.target == null ? "" : ctx.target.toString());
                buf.writeFloat(ctx.damageDealt);
                buf.writeBoolean(ctx.knockedOut);
            }
        }
    }

    @Override
    public ContextMessage onMessage(ContextMessage message, MessageContext ctx) {
        ContextManager.INSTANCE.receiveContexts(message.contexts);
        return null;
    }
}