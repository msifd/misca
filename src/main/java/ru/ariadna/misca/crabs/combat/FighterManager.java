package ru.ariadna.misca.crabs.combat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class FighterManager {

    @SideOnly(Side.CLIENT)
    public static Fighter makeFighterClient(int entityId) {
        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
        if (entity != null && entity instanceof EntityLivingBase) {
            return makeFighter((EntityLivingBase) entity);
        }
        return null;
    }

    public static Fighter makeFighter(EntityLivingBase entity) {
        // TODO Get charsheet
        return new Fighter(null, entity);
    }
}
