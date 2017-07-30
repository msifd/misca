package ru.ariadna.misca.things;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemAriadnaDoor extends ItemDoor {
    private final BlockAriadnaDoor block;

    ItemAriadnaDoor(String id_base, int index, BlockAriadnaDoor block) {
        super(Material.wood);
        this.block = block;

        setUnlocalizedName(id_base + index);
        setTextureName("misca:" + id_base + index);
        setCreativeTab(MiscaThings.tab);
    }

    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        if (p_77648_7_ != 1) {
            return false;
        } else {
            ++p_77648_5_;

            if (p_77648_2_.canPlayerEdit(p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_1_) && p_77648_2_.canPlayerEdit(p_77648_4_, p_77648_5_ + 1, p_77648_6_, p_77648_7_, p_77648_1_)) {
                if (!block.canPlaceBlockAt(p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_)) {
                    return false;
                } else {
                    int i1 = MathHelper.floor_double((double) ((p_77648_2_.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
                    placeDoorBlock(p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_, i1, block);
                    --p_77648_1_.stackSize;
                    return true;
                }
            } else {
                return false;
            }
        }
    }
}
