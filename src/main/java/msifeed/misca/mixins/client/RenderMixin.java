package msifeed.misca.mixins.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Render.class)
public interface RenderMixin<T extends Entity> {
    @Invoker("renderLivingLabel")
    void callRenderLivingLabel(T entityIn, String str, double x, double y, double z, int maxDistance);
}
