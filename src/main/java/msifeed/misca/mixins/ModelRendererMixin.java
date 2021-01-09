package msifeed.misca.mixins;

import net.minecraft.client.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelRenderer.class)
public interface ModelRendererMixin {
    @Accessor
    int getTextureOffsetX();

    @Accessor
    int getTextureOffsetY();
}
