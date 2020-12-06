package msifeed.mellow.sprite;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class SizedTexture extends AbstractTexture {
    private static final Logger LOGGER = LogManager.getLogger();

    public final ResourceLocation location;
    private int width;
    private int height;

    public SizedTexture(ResourceLocation location) {
        this.location = location;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        deleteGlTexture();

        IResource iresource = null;
        try {
            iresource = resourceManager.getResource(location);
            final BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            this.width = bufferedimage.getWidth();
            this.height = bufferedimage.getHeight();

            boolean blur = false;
            boolean clamp = false;
            if (iresource.hasMetadata()) {
                try {
                    final TextureMetadataSection metadata = iresource.getMetadata("texture");
                    if (metadata != null) {
                        blur = metadata.getTextureBlur();
                        clamp = metadata.getTextureClamp();
                    }
                } catch (RuntimeException e) {
                    LOGGER.warn("Failed reading metadata of: {}", location, e);
                }
            }

            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, blur, clamp);
        } finally {
            IOUtils.closeQuietly(iresource);
        }
    }
}
