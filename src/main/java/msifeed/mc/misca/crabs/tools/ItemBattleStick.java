package msifeed.mc.misca.crabs.tools;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import msifeed.mc.misca.crabs.EntityUtils;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.things.MiscaThings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBattleStick extends Item {
    public ItemBattleStick() {
        setUnlocalizedName("battle_stick");
        setTextureName("blaze_rod");
        setCreativeTab(MiscaThings.tab);
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advanced) {
        String desc = LanguageRegistry.instance().getStringLocalization("item.battle_stick.desc");
        desc = StringEscapeUtils.unescapeJava(desc);
        lines.addAll(Arrays.asList(desc.split("\n")));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
        // Обработка только на сервере
        // ЛКМ палкой по существу - добавление в битву
        if (player.worldObj.isRemote || !(target instanceof EntityLivingBase)) return false;

        EntityLivingBase entity = (EntityLivingBase) target;
        if (entity instanceof EntityLiving) ((EntityLiving) entity).playLivingSound();
        BattleManager.INSTANCE.joinBattle(entity);

        return true;
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        // Обработка только на сервере
        // ПКМ палкой по существу:  со сником - исключение из битвы, без - контроль действий
        if (event.entityPlayer.worldObj.isRemote || !(event.target instanceof EntityLivingBase)) return;

        ItemStack itemStack = event.entityPlayer.getHeldItem();
        if (itemStack == null || !(itemStack.getItem() instanceof ItemBattleStick)) return;

        if (event.entityPlayer.isSneaking()) {
            BattleManager.INSTANCE.leaveBattle((EntityLivingBase) event.target, true);
        }
        else {
            BattleManager.INSTANCE.toggleControl(event.entityPlayer, (EntityLivingBase) event.target);
        }
    }
}