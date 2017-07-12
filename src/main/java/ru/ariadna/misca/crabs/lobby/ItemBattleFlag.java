package ru.ariadna.misca.crabs.lobby;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import org.apache.commons.lang3.StringEscapeUtils;
import ru.ariadna.misca.Misca;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.gui.CrabsGuiHandler;
import ru.ariadna.misca.things.MiscaThings;

import java.util.Arrays;
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
        String desc = LanguageRegistry.instance().getStringLocalization("item.battle_flag.desc");
        desc = StringEscapeUtils.unescapeJava(desc);
        lines.addAll(Arrays.asList(desc.split("\n")));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
        // Обработка только на сервере
        // ЛКМ флагом по существу - добавление в лобби
        if (!(player instanceof EntityPlayerMP) || !(target instanceof EntityLivingBase))
            return false;

        EntityLivingBase entity = (EntityLivingBase) target;
        if (entity instanceof EntityLiving) ((EntityLiving) entity).playLivingSound();
        Crabs.instance.lobbyManager.includeToPlayersLobby((EntityPlayerMP) player, entity);

        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        // Открытие лобби или боя
        if (!player.isSneaking()) {
            int gui_id = Crabs.instance.fighterManager.isInFight(player) ? CrabsGuiHandler.GuiTypes.COMBAT.id() : CrabsGuiHandler.GuiTypes.LOBBY.id();
            ChunkCoordinates c = player.getPlayerCoordinates();
            player.openGui(Misca.instance(), gui_id, player.getEntityWorld(), c.posX, c.posY, c.posZ);
        }

        return super.onItemRightClick(itemStack, world, player);
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        // Обработка только на сервере
        // ПКМ флагом со сником по существу - исключение из лобби
        if (!(event.entityPlayer instanceof EntityPlayerMP)
                || !(event.entityPlayer.getHeldItem().getItem() instanceof ItemBattleFlag)
                || !(event.target instanceof EntityLivingBase)
                || !event.entityPlayer.isSneaking())
            return;

        EntityLivingBase entity = (EntityLivingBase) event.target;
        if (entity instanceof EntityLiving) ((EntityLiving) entity).playLivingSound();
        Crabs.instance.lobbyManager.excludeFromPlayersLobby((EntityPlayerMP) event.entityPlayer, entity);
    }
}
