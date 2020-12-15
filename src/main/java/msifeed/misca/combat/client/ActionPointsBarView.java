package msifeed.misca.combat.client;

import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.utils.Geom;
import msifeed.mellow.widget.View;
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
    public void render() {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ICombatant com = CombatantProvider.get(player);

        final double fullAp = com.getActionPoints();
        final double moveAp = Rules.movementActionPoints(com.getPosition(), player.getPositionVector());
        final double actionAp = Rules.attackActionPoints(player);
        final double ap = Math.max(com.getActionPoints() - moveAp, 0);

        final int bgColor = ap > 0 ? 0xffffffff : 0xffff0000;
        final int barWidth = this.geometry.w - 2;
        final double pxPerAp = barWidth / fullAp;

        RenderShapes.rect(this.geometry, bgColor);

        final Geom geom = this.geometry.clone();
        geom.add(1, 1, 0, -2);
        geom.w = (int) (ap * pxPerAp);
        RenderShapes.rect(geom, 0xff000000);

        geom.setSize(1, 5);
        double consumedAp = 0;
        double nextAp = actionAp + com.getActionPointsOverhead();
        while (ap >= consumedAp + nextAp) {
            geom.x = (int) (this.geometry.x + (consumedAp + nextAp) * pxPerAp);
            RenderShapes.rect(geom, bgColor);

            consumedAp += nextAp;
            nextAp += nextAp / 2;
        }
    }
}
