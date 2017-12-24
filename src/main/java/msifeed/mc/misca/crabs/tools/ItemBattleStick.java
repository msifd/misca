package msifeed.mc.misca.crabs.tools;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import msifeed.mc.misca.crabs.context.Context;
import msifeed.mc.misca.crabs.context.ContextManager;
import msifeed.mc.misca.crabs.fight.FightManager;
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
        // ЛКМ палкой по существу: со сником - сброс, без - добавление в битву
        if (player.worldObj.isRemote || !(target instanceof EntityLivingBase)) return false;

        final EntityLivingBase entity = (EntityLivingBase) target;
        final Context actor = ContextManager.INSTANCE.getOrCreateContext(entity);

        if (player.isSneaking()) {
            ContextManager.INSTANCE.resetContext(actor);
        } else {
            if (entity instanceof EntityLiving) ((EntityLiving) entity).playLivingSound();
            FightManager.INSTANCE.joinFight(actor);
        }

        return true;
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        // Обработка только на сервере
        // ПКМ палкой по существу:  со сником - исключение из битвы, без - контроль действий
        if (event.entityPlayer.worldObj.isRemote || !(event.target instanceof EntityLivingBase)) return;

        final ItemStack itemStack = event.entityPlayer.getHeldItem();
        if (itemStack == null || !(itemStack.getItem() instanceof ItemBattleStick)) return;

        final EntityLivingBase entity = (EntityLivingBase) event.target;
        final Context actor = ContextManager.INSTANCE.getContext(entity);
        if (actor == null) return;

        if (event.entityPlayer.isSneaking()) {
            FightManager.INSTANCE.leaveFight(actor, true);
        } else {
            final Context context = ContextManager.INSTANCE.getContext(event.entityPlayer);
            FightManager.INSTANCE.toggleControl(context, actor);
        }
    }
}
