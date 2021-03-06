package msifeed.misca.charstate.potion;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharAttribute;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AttributePotions {
    public static final Potion STR_INC = create(CharAttribute.str, "strInc", 0xff7070, "c305fe8c-d8eb-4c35-a5a4-fcf1b9845b4e", 1, 0);
    public static final Potion STR_DEC = create(CharAttribute.str, "strDec", 0x227070, "9d29ee68-5a72-4ce2-8d45-242c421b68d1", -1, 0);
    public static final Potion STR_GRO = create(CharAttribute.str, "strGrow", 0xff7070, "8d0b4743-cdf1-46cd-bffe-fb956873edc0", 0.01, 2);
    public static final Potion STR_RED = create(CharAttribute.str, "strReduce", 0x227070, "2b1b8b41-a62e-4b02-9b3b-1f8d10127df3", -0.01, 2);

    public static final Potion PER_INC = create(CharAttribute.per, "perInc", 0x707070, "b3045a5f-2606-44fe-986e-9de5fb17e635", 1, 0);
    public static final Potion PER_DEC = create(CharAttribute.per, "perDec", 0x707070, "739e309a-ec80-4882-a7a4-8dfaa38b326c", -1, 0);
    public static final Potion PER_GRO = create(CharAttribute.per, "perGrow", 0x707070, "13d5729f-1b8e-4336-8464-cea3d7b8b854", 0.01, 2);
    public static final Potion PER_RED = create(CharAttribute.per, "perReduce", 0x707070, "63acf772-2faf-49e9-a366-08103445ffd9", -0.01, 2);

    public static final Potion END_INC = create(CharAttribute.end, "endInc", 0x707070, "4fc3035c-86de-44d7-ab33-4058bd065810", 1, 0);
    public static final Potion END_DEC = create(CharAttribute.end, "endDec", 0x707070, "8f4ad0c0-a30e-4668-8e94-76bab5d73dcc", -1, 0);
    public static final Potion END_GRO = create(CharAttribute.end, "endGrow", 0x707070, "7b9fe9e6-3c24-484f-88f2-790e1a5928b2", 0.01, 2);
    public static final Potion END_RED = create(CharAttribute.end, "endReduce", 0x707070, "8db5a650-6611-4ce2-9463-fc0c73a51ec1", -0.01, 2);

    public static final Potion REF_INC = create(CharAttribute.ref, "refInc", 0x707070, "e1baeb64-fa8c-49b7-8adb-01c2d01e19de", 1, 0);
    public static final Potion REF_DEC = create(CharAttribute.ref, "refDec", 0x707070, "f0c1d333-aa82-406a-8b26-f09ad597b011", -1, 0);
    public static final Potion REF_GRO = create(CharAttribute.ref, "refGrow", 0x707070, "2a03d2c0-9b2d-4489-863d-57c3a85948e7", 0.01, 2);
    public static final Potion REF_RED = create(CharAttribute.ref, "refReduce", 0x707070, "d8d9a072-2451-4b7c-8b66-cc0db14dddc2", -0.01, 2);

    public static final Potion AGI_INC = create(CharAttribute.agi, "agiInc", 0x707070, "a7c4ed76-8edc-4189-bb9f-2179ae78597f", 1, 0);
    public static final Potion AGI_DEC = create(CharAttribute.agi, "agiDec", 0x707070, "26e5a1ca-f8d9-4a47-aac3-5af1837b7ca4", -1, 0);
    public static final Potion AGI_GRO = create(CharAttribute.agi, "agiGrow", 0x707070, "0e81b2a9-c431-4ab6-aede-5b854e73ecd9", 0.01, 2);
    public static final Potion AGI_RED = create(CharAttribute.agi, "agiReduce", 0x707070, "ef82c6c2-c421-4346-b63d-d7260503a964", -0.01, 2);

    public static final Potion LCK_INC = create(CharAttribute.lck, "lckInc", 0x707070, "f2320dd2-d0cc-4cdf-bbb7-cac5832138a5", 1, 0);
    public static final Potion LCK_DEC = create(CharAttribute.lck, "lckDec", 0x707070, "18642c5f-8bcd-430f-9a6c-4445904cc041", -1, 0);
    public static final Potion LCK_GRO = create(CharAttribute.lck, "lckGrow", 0x707070, "0188d94d-5a09-425d-9367-f2b7df9ca573", 0.01, 2);
    public static final Potion LCK_RED = create(CharAttribute.lck, "lckReduce", 0x707070, "cb42d5f8-b023-4fa6-bce3-bf95c74c37fa", -0.01, 2);

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(STR_INC);
        event.getRegistry().register(STR_DEC);
        event.getRegistry().register(STR_GRO);
        event.getRegistry().register(STR_RED);
        event.getRegistry().register(PER_INC);
        event.getRegistry().register(PER_DEC);
        event.getRegistry().register(PER_GRO);
        event.getRegistry().register(PER_RED);
        event.getRegistry().register(END_INC);
        event.getRegistry().register(END_DEC);
        event.getRegistry().register(END_GRO);
        event.getRegistry().register(END_RED);
        event.getRegistry().register(REF_INC);
        event.getRegistry().register(REF_DEC);
        event.getRegistry().register(REF_GRO);
        event.getRegistry().register(REF_RED);
        event.getRegistry().register(AGI_INC);
        event.getRegistry().register(AGI_DEC);
        event.getRegistry().register(AGI_GRO);
        event.getRegistry().register(AGI_RED);
        event.getRegistry().register(LCK_INC);
        event.getRegistry().register(LCK_DEC);
        event.getRegistry().register(LCK_GRO);
        event.getRegistry().register(LCK_RED);
    }

    private static Potion create(CharAttribute attr, String name, int color, String uuid, double amount, int op) {
        return new TemporaryNeedsPotion(amount < 0, color)
                .setPotionName("effect." + name).setRegistryName(Misca.MODID, name)
                .registerPotionAttributeModifier(attr.attribute, uuid, amount, op);
    }
}
