package msifeed.mc.misca.crabs;

import msifeed.mc.misca.crabs.rules.DiceMath;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
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
//                .map(i -> DiceMath.g15())
//                .map(i -> DiceMath.g40())
//                .map(i -> DiceMath.g40_plus())
//                .map(i -> DiceMath.d20())
                .map(i -> DiceMath.g20())
                .boxed()
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()))
                .forEach((i, c) -> {
                    float percent = c / (float) total * 100;
                    int bars_n = Math.round(percent);
                    String bars = String.join("", Collections.nCopies(bars_n, "#"));
                    System.out.println(String.format("%2d: %4.1f%% %s", i, percent, bars));
                });
    }

    @Ignore
    @Test
    public void dice_distribution_threshold() {
        final int total = 100000;
        final int threshold = 18;

        for (int s = 1; s <= 19; s++) {
            final int statThreshold = threshold - s;
            long succeeded = java.util.stream.IntStream.range(0, total)
                    .map(i -> DiceMath.d20())
//                    .map(i -> DiceMath.g20())
//                    .map(i -> DiceMath.g15())
                    .boxed()
                    .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()))
                    .entrySet().stream()
                    .filter(e -> e.getKey() >= statThreshold)
                    .mapToLong(Map.Entry::getValue).sum();

            float percent = succeeded / (float) total * 100;
            int bars_n = Math.round(percent / 2f);
            String bars = String.join("", Collections.nCopies(bars_n, "#"));
            System.out.println(String.format("%2d|%d: %5.1f%% %s", s, threshold, percent, bars));
        }
    }
}
