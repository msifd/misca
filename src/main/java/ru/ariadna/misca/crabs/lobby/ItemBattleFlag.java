package ru.ariadna.misca.crabs.lobby;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.things.MiscaThings;

import java.util.List;

public class ItemBattleFlag extends Item {
    ItemBattleFlag() {
        setUnlocalizedName("battle_flag");
        setTextureName("misca:battle_flag");
        setCreativeTab(MiscaThings.tab);
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advanced) {
        lines.add(LanguageRegistry.instance().getStringLocalization("item.battle_flag.desc.1"));
        lines.add(LanguageRegistry.instance().getStringLocalization("item.battle_flag.desc.2"));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!(entity instanceof EntityLivingBase))
            return false;

        Crabs.logger.info("left interact with {}", entity);


        return true;
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (!(event.entityPlayer.getHeldItem().getItem() instanceof ItemBattleFlag)
                || !(event.target instanceof EntityLivingBase))
            return;

        Entity entity = event.target;
        Character character;

        Crabs.logger.info("right interact with {}", entity);

        if (entity instanceof EntityPlayer) {

        }
    }
}
