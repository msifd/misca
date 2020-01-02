package msifeed.mc.aorta.genesis.items.templates;

import msifeed.mc.aorta.genesis.GenesisTrait;
import msifeed.mc.aorta.genesis.items.IItemTemplate;
import msifeed.mc.aorta.genesis.items.ItemCommons;
import msifeed.mc.aorta.genesis.items.ItemGenesisUnit;
import msifeed.mc.aorta.genesis.items.data.ArmorData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ArmorTemplate extends ItemArmor implements IItemTemplate {
    private final ItemGenesisUnit unit;
    private final ArmorData armorData;

    public ArmorTemplate(ItemGenesisUnit unit, ArmorData armorData) {
        super(ArmorMaterial.CHAIN, 0, getArmorType(unit));
        this.unit = unit;
        this.armorData = armorData;
        setUnlocalizedName(unit.id);
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        final String name = unit.title != null
                ? unit.title
                : super.getItemStackDisplayName(itemStack);
        return unit.rarity.color.toString() + name;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean debug) {
        ItemCommons.addInformation(unit, itemStack, lines);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (armorData.texture.isEmpty())
            return super.getArmorTexture(stack, entity, slot, type);
        return armorData.texture + "_" + (slot == 2 ? 2 : 1) + ".png";
    }

    @Override
    public ItemGenesisUnit getUnit() {
        return unit;
    }

    private static int getArmorType(ItemGenesisUnit unit) {
        if (unit.hasTrait(GenesisTrait.helmet))
            return 0;
        if (unit.hasTrait(GenesisTrait.plate))
            return 1;
        if (unit.hasTrait(GenesisTrait.legs))
            return 2;
        if (unit.hasTrait(GenesisTrait.boots))
            return 3;
        return 0;
    }
}
