package msifeed.misca.potions;

import msifeed.misca.Misca;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OtherPotions {
    public static final Potion spiderClimbing = new TagPotion(false, 0x770077)
            .setRegistryName(Misca.MODID, "spiderClimbing")
            .setPotionName("effect.misca.spiderClimbing");

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(spiderClimbing);
    }
}
