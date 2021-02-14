package msifeed.misca.combat.client;

import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.utils.Geom;
import msifeed.mellow.view.View;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ActionPointsBarView extends View {
    ActionPointsBarView() {
        setSize(100, 7);
    }

    @Override
    public void render(Geom geom) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ICombatant com = CombatantProvider.get(player);

        final Rules rules = Combat.getRules();
        final double fullAp = com.getActionPoints();
        final double moveAp = rules.movementActionPoints(com.getPosition(), player.getPositionVector());
        final double actionAp = rules.attackActionPoints(player);
        final double ap = Math.max(com.getActionPoints() - moveAp, 0);

        final int bgColor = ap > 0 ? 0xffffffff : 0xffff0000;
        final int barWidth = geom.w - 2;
        final double pxPerAp = barWidth / fullAp;

        RenderShapes.rect(geom, bgColor);

        final Geom barGeom = geom.clone();
        barGeom.add(1, 1, 0, -2);
        barGeom.w = (int) (ap * pxPerAp);
        RenderShapes.rect(barGeom, 0xff000000);

        barGeom.setSize(1, 5);
        double consumedAp = 0;
        double nextAp = actionAp + com.getActionPointsOverhead();
        while (ap >= consumedAp + nextAp) {
            barGeom.x = (int) (geom.x + (consumedAp + nextAp) * pxPerAp);
            RenderShapes.rect(barGeom, bgColor);

            consumedAp += nextAp;
            nextAp += nextAp / 2;
        }
    }
}
