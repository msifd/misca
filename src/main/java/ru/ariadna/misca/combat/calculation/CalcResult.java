package ru.ariadna.misca.combat.calculation;

import com.google.common.base.Joiner;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CalcResult {
    int result;
    List<Integer> rolls = new LinkedList<>();
    List<Integer> stats = new LinkedList<>();
    List<Float> coeffs = new LinkedList<>();
    int mods = 0;

    public int getResult() {
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(Joiner.on(" + ").join(rolls));
        sb.append("]");

        Iterator<Integer> s_iter = stats.listIterator();
        Iterator<Float> c_iter = coeffs.listIterator();
        while (s_iter.hasNext()) {
            sb.append(" + ");
            sb.append(s_iter.next());
            sb.append("*");
            sb.append(c_iter.next());
        }

        if (mods != 0) {
            sb.append(mods > 0 ? " + " : " - ");
            sb.append(Math.abs(mods));
        }

        sb.append(" = ");
        sb.append(result);

        return sb.toString();
    }
}
