package msifeed.mc.misca.crabs;

import msifeed.mc.misca.crabs.rules.DiceMath;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiceTest {
    @Ignore
    @Test
    public void dice_distribution() {
        int total = 1000000;
        java.util.stream.IntStream.range(0, total)
//                .map(i -> (int) Math.floor(gauss(4.5, 1, 21))) // fight roll
//                .map(i -> (int) Math.round(gauss(4.5, 1, 21) / 2.)) // stat roll
                .map(i -> DiceMath.g30())
//                .map(i -> DiceMath.g30_plus())
                .boxed()
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()))
                .forEach((i, c) -> {
                    float percent = c / (float) total * 100;
                    int bars_n = Math.round(percent);
                    String bars = String.join("", Collections.nCopies(bars_n, "#"));
                    System.out.println(String.format("%2d: %4.1f%% %s", i, percent, bars));
                });
    }
}
