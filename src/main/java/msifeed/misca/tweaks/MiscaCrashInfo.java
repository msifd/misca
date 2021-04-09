package msifeed.misca.tweaks;

import net.minecraftforge.fml.common.ICrashCallable;

public class MiscaCrashInfo implements ICrashCallable {
    private static final int MIN_STACK_LENGTH = 10;

    @Override
    public String getLabel() {
        return "Misca";
    }

    @Override
    public String call() throws Exception {
        final StringBuilder sb = new StringBuilder("Step brother I'm stuck!\n");
        addThreadsInfo(sb);
        return sb.toString();
    }

    private void addThreadsInfo(StringBuilder sb) {
        final Thread currentThread = Thread.currentThread();

        Thread.getAllStackTraces().forEach((thread, stack) -> {
            if (thread == currentThread) return;
            if (stack.length < MIN_STACK_LENGTH) return;

            sb.append(String.format("\t\t%s:%d\n", thread.getName(), thread.getId()));
            for (StackTraceElement line : stack) {
                sb.append("\t\t\t");
                sb.append(line.toString());
                sb.append('\n');
            }
            sb.append("\n");
        });
    }
}
