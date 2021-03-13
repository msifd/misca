package msifeed.misca.combat.client;

import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.utils.Geom;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import net.minecraft.entity.EntityLivingBase;

public class ActionPointsBarView {
    public static void render(Geom geom, EntityLivingBase entity) {
        final ICombatant com = CombatantProvider.get(entity);

        final Rules rules = Combat.getRules();
        final double fullAp = com.getActionPoints();
        final double moveAp = rules.movementActionPoints(com.getPosition(), entity.getPositionVector());
        final double actionAp = rules.attackActionPoints(entity);
        final double ap = Math.max(com.getActionPoints() - moveAp, 0);

        final int bgColor = ap > 0 ? 0xffffff00 : 0xffff0000;
        final int barWidth = geom.w - 2;
        final double pxPerAp = barWidth / fullAp;

        final Geom barGeom = geom.clone();

        barGeom.w = (int) (ap * pxPerAp);
        RenderShapes.rect(barGeom, bgColor);

        barGeom.w = 1;
        double consumedAp = 0;
        double nextAp = actionAp + com.getActionPointsOverhead();
        while (ap >= consumedAp + nextAp) {
            barGeom.x = (int) (geom.x + (consumedAp + nextAp) * pxPerAp);
            RenderShapes.rect(barGeom, 0xff000000);

            consumedAp += nextAp;
            nextAp += nextAp / 2;
        }
    }
}
