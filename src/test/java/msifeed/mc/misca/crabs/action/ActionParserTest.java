package msifeed.mc.misca.crabs.action;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.rules.Buff;
import msifeed.mc.misca.crabs.rules.DynamicEffect;
import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Modifier;
import msifeed.mc.misca.crabs.rules.Rules;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ActionParserTest {
    private static Action getPointHitAction() {
        Action act = new Action("point_hit", "Point hit", Action.Type.MELEE);
        Collections.addAll(act.modifiers,
                new Modifier.DiceG40Plus(),
                new Modifier.Stat(Stats.STR),
                new Modifier.Stat(Stats.PER),
                new Modifier.Stat(Stats.INT));
        Collections.addAll(act.target_effects,
                new Effect.Damage());
        return act;
    }

    @Test
    public void test_json() throws IOException {
        URL url = Resources.getResource("action1.json");
        String res = Resources.toString(url, Charsets.UTF_8);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Action.class, new ActionJsonSerializer())
                .create();

        Action act = gson.fromJson(res, Action.class);
        Action exp = getPointHitAction();

        assertEquals(exp, act);
    }

    @Test
    public void dynamicEffect() {
        final String src = "score:+5";
        final DynamicEffect.Score effect = (DynamicEffect.Score) Rules.effect(src);

        assertNotNull(effect);
        assertEquals(effect.name(), "score");
//        assertEquals(effect.value, 5);
    }

    @Test
    public void buff() {
        final String src = "buff:1:1:score:+5";
        final Buff buff = (Buff) Rules.effect(src);

        assertNotNull(buff);
        assertEquals(buff.effect, Rules.effect("score:+5"));

        assertTrue(!buff.active());
        assertTrue(!buff.started());
        assertTrue(!buff.ended());
        buff.step();
        assertTrue(buff.active());
        assertTrue(buff.started());
        assertTrue(!buff.ended());
        buff.step();
        assertTrue(buff.active());
        assertTrue(buff.started());
        assertTrue(!buff.ended());
        buff.step();
        assertTrue(!buff.active());
        assertTrue(buff.started());
        assertTrue(buff.ended());
    }
}
