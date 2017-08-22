package ru.ariadna.misca.twowayradio;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.input.Mouse;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.things.MiscaThings;

import java.util.List;

public class ItemWalkieTalkie extends Item {
    private static final NBTTagCompound itemNbtDefaults = new NBTTagCompound();
    static final short FQ_RANGE_LOW = 50, FQ_RANGE_HIGH = 550, FQ_STEP = 5;
    static final byte PRE_ADJUSTMENT_TICKS = 10;
    static float adjusted_fq = 0;
    static boolean adjusting = false;

    static {
        itemNbtDefaults.setFloat("fq", (FQ_RANGE_LOW + FQ_RANGE_HIGH) / 2);
    }

    private IIcon disabledIcon;

    private ItemWalkieTalkie() {
        setUnlocalizedName("walkie-talkie");
        setTextureName("misca:walkie-talkie");
        setCreativeTab(MiscaThings.tab);
        setMaxStackSize(1);
    }

    static void register() {
        ItemWalkieTalkie item = new ItemWalkieTalkie();
        MinecraftForge.EVENT_BUS.register(item);
        GameRegistry.registerItem(item, "walkie-talkie");
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        if (itemStack.stackTagCompound == null)
            itemStack.stackTagCompound = (NBTTagCompound) itemNbtDefaults.copy();
        final float frequency = itemStack.stackTagCompound.getFloat("fq");
        final float ratio = (frequency - FQ_RANGE_LOW) / (FQ_RANGE_HIGH - FQ_RANGE_LOW);
        return 1.0 - ratio;
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack) {
        return itemStack.getItemDamage() > 0;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean debug) {
        if (itemStack.stackTagCompound == null)
            itemStack.stackTagCompound = (NBTTagCompound) itemNbtDefaults.copy();

        final boolean enabled = itemStack.getItemDamage() > 0;
        final String is_enabled = MiscaUtils.l10n(enabled ? "misca.walkie-talkie.item.enabled" : "misca.walkie-talkie.item.disabled");
        lines.add(is_enabled);

        if (enabled) {
            final float frequency = itemStack.stackTagCompound.getFloat("fq");
            lines.add(MiscaUtils.l10n("misca.walkie-talkie.item.fq", frequency));
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;

        final ItemStack itemStack = event.entityPlayer.getHeldItem();
        if (itemStack == null || !(itemStack.getItem() instanceof ItemWalkieTalkie))
            return;

        final EntityPlayer player = event.entityPlayer;
        if (player.isSneaking() && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && !adjusting) {
            event.useItem = Event.Result.DENY;
            event.useBlock = Event.Result.ALLOW;
            return;
        }

        final boolean enabled = itemStack.getItemDamage() > 0;
        if (!enabled) {
            itemStack.setItemDamage(1);
        } else {
            if (!player.isUsingItem())
                player.setItemInUse(itemStack, Integer.MAX_VALUE);

            if (FMLCommonHandler.instance().getSide().isClient()) {
                adjusted_fq = itemStack.stackTagCompound.getFloat("fq");
            }
        }

        event.setCanceled(true);
    }

    @Override
    public void onUsingTick(ItemStack itemStack, EntityPlayer player, int count) {
        final boolean enabled = itemStack.getItemDamage() > 0;
        if (enabled && Integer.MAX_VALUE - count > PRE_ADJUSTMENT_TICKS && FMLCommonHandler.instance().getSide().isClient()) {
            adjusting = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseEvent(MouseEvent event) {
        if (!adjusting || event.dx == 0) return;

        double step = 0.01 * Math.pow(event.dx, 2);
        float next_fq = adjusted_fq + (float) Math.copySign(step, event.dx);
        adjusted_fq = Math.max(FQ_RANGE_LOW, Math.min(FQ_RANGE_HIGH, next_fq));

        Minecraft.getMinecraft().mouseHelper.deltaX = 0;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int delta) {
        if (Integer.MAX_VALUE - delta <= PRE_ADJUSTMENT_TICKS) {
            itemStack.setItemDamage(0);
            return;
        } else if (FMLCommonHandler.instance().getSide().isClient()) {
            itemStack.stackTagCompound.setFloat("fq", adjusted_fq);
        }

        adjusting = false;
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        return meta > 0 ? itemIcon : disabledIcon;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        disabledIcon = iconRegister.registerIcon("misca:walkie-talkie_disabled");
    }
}
