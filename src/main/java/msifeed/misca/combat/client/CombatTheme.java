package msifeed.misca.combat.client;

import msifeed.mellow.sprite.FlatSprite;
import msifeed.mellow.sprite.SizedTexture;
import msifeed.misca.Misca;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class CombatTheme {
    public static SizedTexture tex = new SizedTexture(new ResourceLocation(Misca.MODID, "theme/combat.png"));

    public static FlatSprite combatantFrame = new FlatSprite(tex,6, 3, 36, 42);

    public static void load() {
        Minecraft.getMinecraft().getTextureManager().loadTexture(tex.location, tex);
    }
}
