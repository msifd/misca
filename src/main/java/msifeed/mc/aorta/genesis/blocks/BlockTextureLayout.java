package msifeed.mc.aorta.genesis.blocks;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;

import java.util.Arrays;
import java.util.List;

public class BlockTextureLayout {
    private List<String> rawTextures;
    private IIcon[] textures;
    private int[] textureLayout;

    public BlockTextureLayout(List<String> rawTextures, int[] layout) {
        this.rawTextures = rawTextures;
        this.textureLayout = Arrays.copyOf(layout, 6);
    }

    public IIcon getIcon(int side) {
        return textures[textureLayout[side]];
    }

    public IIcon getRotatableIcon(int side, int meta) {
        return textures[textureLayout[BlockTraitCommons.getRotatedSide(side, meta)]];
    }

    public IIcon getPillarIcon(int side, int meta) {
        // TODO: make really rotatable pillar
        final IIcon topIcon = textures[textureLayout[1]];
        final IIcon sideIcon = textures[textureLayout[2]];
        final int ort = BlockTraitCommons.getPillarOrt(meta);
        final int oppositeOrt = Facing.oppositeSide[ort];
        if (side == ort || side == oppositeOrt)
            return topIcon;
        else
            return sideIcon;
    }

    public void registerBlockIcons(IIconRegister register) {
        textures = rawTextures.stream()
                .map(register::registerIcon)
                .toArray(IIcon[]::new);
    }
}
