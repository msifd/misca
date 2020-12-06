package msifeed.misca.client;

import msifeed.mellow.sprite.ISprite;
import msifeed.mellow.sprite.SizedTexture;
import msifeed.mellow.sprite.Slice9Sprite;
import msifeed.misca.Misca;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class MiscaTheme {
    public static SizedTexture uiTex = new SizedTexture(new ResourceLocation(Misca.MODID, "theme/ui.png"));
//    public static SizedTexture combatTex = new SizedTexture(new ResourceLocation(Misca.MODID, "theme/combat.png"));

    public static ISprite window = new Slice9Sprite(uiTex,0, 0, 3,15, 7,4, 3,5);

    public static void load() {
        Minecraft.getMinecraft().getTextureManager().loadTexture(uiTex.location, uiTex);


    }
}
