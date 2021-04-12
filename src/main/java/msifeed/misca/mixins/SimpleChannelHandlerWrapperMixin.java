package msifeed.misca.mixins;

import io.netty.channel.ChannelHandlerContext;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleChannelHandlerWrapper.class, remap = false)
public class SimpleChannelHandlerWrapperMixin {
    @Final
    @Shadow
    private IMessageHandler<?, ?> messageHandler;

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause, CallbackInfo ci) {
        FMLLog.log.error("Responsible IMessageHandler type: " + messageHandler.getClass().getName());
    }
}
