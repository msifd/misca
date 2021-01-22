package msifeed.mellow.view.text.backend;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;

import java.util.ArrayList;

public class AutoCompleter {
    protected final TextEditorBackend backend;

    protected boolean didComplete;
    protected boolean requestedCompletions;
    protected boolean gotCompletions;

    protected ArrayList<String> completions = new ArrayList<>();
    protected int completionIdx;

    public AutoCompleter(TextEditorBackend backend) {
        this.backend = backend;
    }

    public void reset() {
        didComplete = false;
        requestedCompletions = false;
        gotCompletions = false;
    }

    public void complete() {
        if (!requestedCompletions) {
            completions.clear();
            completionIdx = 0;

            final String req = backend.toJoinedString()
                    .substring(0, backend.getAbsoluteCursor())
                    .replaceAll("\\s", " ");
            requestCompletions(req);
            return;
        } else if (completions.isEmpty()) {
            return;
        }

        if (!backend.isCursorCharacter(-1, Character::isWhitespace)) {
            backend.remove(-backend.getPrevWordLength());
        }

        final String completion = TextFormatting.getTextWithoutFormattingCodes(completions.get(completionIdx));
        backend.insert(completion);

        completionIdx++;
        if (completionIdx >= completions.size())
            completionIdx = 0;
    }

    private void requestCompletions(String prefix) {
        if (prefix.isEmpty()) return;

        net.minecraftforge.client.ClientCommandHandler.instance.autoComplete(prefix);
        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketTabComplete(prefix, null, false));
        requestedCompletions = true;
    }

    public void setCompletions(String[] newCompletions) {
        if (!requestedCompletions) return;

        didComplete = false;
        gotCompletions = true;

        completions.clear();

        final String[] clientCompletions = ClientCommandHandler.instance.latestAutoComplete;
        if (clientCompletions != null) {
            for (String s : clientCompletions) {
                if (!s.isEmpty())
                    completions.add(s);
            }
        }

        for (String s : newCompletions) {
            if (!s.isEmpty()) completions.add(s);
        }

        if (!completions.isEmpty()) {
            complete();
        }

    }
}
