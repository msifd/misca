package msifeed.mc.misca.crabs;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import msifeed.mc.misca.crabs.actions.Action;
import msifeed.mc.misca.crabs.actions.ActionJsonDeserializer;
import msifeed.mc.misca.crabs.actions.ActionParser;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Roll;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

public class ActionParserTest {
    @Ignore
    @Test
    public void test_liner() throws IOException {
        String liner = "point_hit:melee:g30+,STR,PER,INT,-5:damage: : ";
        assertEquals(getPointHitAction(), ActionParser.parse(liner));
    }

    @Test
    public void test_json() throws IOException {
        URL url = Resources.getResource("action1.json");
        String res = Resources.toString(url, Charsets.UTF_8);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Action.class, new ActionJsonDeserializer())
                .create();

        Action act = gson.fromJson(res, Action.class);
        Action exp = getPointHitAction();

        assertEquals(exp, act);
    }

    private static Action getPointHitAction() {
        Action act = new Action("point_hit", Action.Type.MELEE);
        Collections.addAll(act.rolls,
                new Roll.DiceG30Plus(),
                new Roll.Stat(Stats.STR),
                new Roll.Stat(Stats.PER),
                new Roll.Stat(Stats.INT),
                new Roll.Const(-5));
        Collections.addAll(act.target_effects,
                new Effect.Damage());
        return act;
    }
}
