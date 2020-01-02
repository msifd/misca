package msifeed.mc.aorta.genesis.items;

import msifeed.mc.aorta.genesis.GenesisTrait;
import net.minecraft.util.EnumChatFormatting;

public enum ItemRarity {
    POOR(GenesisTrait.poor, EnumChatFormatting.GRAY),
    COMMON(GenesisTrait.common, EnumChatFormatting.WHITE),
    UNCOMMON(GenesisTrait.uncommon, EnumChatFormatting.GREEN),
    RARE(GenesisTrait.rare, EnumChatFormatting.BLUE),
    EPIC(GenesisTrait.epic, EnumChatFormatting.DARK_PURPLE),
    LEGENDARY(GenesisTrait.legendary, EnumChatFormatting.GOLD),
    ;

    public final GenesisTrait trait;
    public final EnumChatFormatting color;

    ItemRarity(GenesisTrait trait, EnumChatFormatting color) {
        this.trait = trait;
        this.color = color;
    }
}
