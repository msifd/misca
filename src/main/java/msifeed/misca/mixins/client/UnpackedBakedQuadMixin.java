package msifeed.misca.mixins.client;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UnpackedBakedQuad.class)
public abstract class UnpackedBakedQuadMixin {
    private static final float[][][] EMPTY = new float[0][][];

    @Accessor(remap = false)
    protected abstract void setUnpackedData(float[][][] value);

    @Shadow
    public abstract int[] getVertexData();

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onConstructed(CallbackInfo ci) {
        getVertexData();
        setUnpackedData(EMPTY);
    }

    @Inject(method = "pipe", at = @At("HEAD"), cancellable = true, remap = false)
    public void pipe(IVertexConsumer consumer, CallbackInfo ci) {
        LightUtil.putBakedQuad(consumer, (BakedQuad) (Object) this);
        ci.cancel();
    }
}
