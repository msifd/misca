package msifeed.mellow.view.text.backend;

import msifeed.mellow.utils.Geom;
import msifeed.mellow.utils.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextEditorBackend {
    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private final Pattern splitter = Pattern.compile("\\R");

    private final List<Line> lines = new ArrayList<>();

    private List<String> viewCache;
    private boolean cacheInvalid = true;
    private int cacheCursorX = 0;

    private final Point cursor = new Point();
    private final Geom view = new Geom(0, 0, 100, 100);
    private int viewWidthReserve = 4;

    private int maxLines = Integer.MAX_VALUE;
    private int maxWidth = Integer.MAX_VALUE;

    public TextEditorBackend() {
        clear();
    }

    public boolean isEmpty() {
        return lines.size() == 1 && lines.get(0).sb.length() == 0;
    }

    public Point getCursor() {
        return cursor;
    }

    public Geom getView() {
        return view;
    }

    public int getCursorXOffset() {
        return cacheCursorX;
    }

    public void setViewWidthReserve(int viewWidthReserve) {
        this.viewWidthReserve = viewWidthReserve;
    }

    public void setLinesPerView(int lines) {
        view.h = lines * fontRenderer.FONT_HEIGHT;
    }

    public int getLinesPerView() {
        return view.h / fontRenderer.FONT_HEIGHT;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int n) {
        this.maxLines = n;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void updateOffsetLine(int lineDelta) {
        view.y = Math.max(0, view.y + lineDelta);
        cacheInvalid = true;
        refreshCursorPos();
    }

    public Line getCurrentLine() {
        return getLine(cursor.y);
    }

    public Line getLine(int n) {
        return lines.get(Math.max(0, Math.min(n, lines.size() - 1)));
    }

    public int getLineCount() {
        return lines.size();
    }

    public List<String> getViewLines() {
        if (cacheInvalid) {
            viewCache = lines.stream()
                    .skip(view.y)
                    .limit(getLinesPerView())
                    .map(ln -> {
                        if (ln.columns > view.x)
                            return fontRenderer.trimStringToWidth(ln.sb.substring(view.x), view.w);
                        else
                            return "";
                    })
                    .collect(Collectors.toList());
            refreshCursorPos();
            cacheInvalid = false;
        }

        return viewCache;
    }

    public Stream<String> getLines() {
        return lines.stream().map(l -> l.sb.toString());
    }

    public Stream<Line> getRawLines() {
        return lines.stream();
    }

    public String toJoinedString() {
        final StringBuilder sb = new StringBuilder();
        for (Line l : lines) {
            sb.append(l.sb);
            sb.append('\n');
        }
        return sb.toString();
    }

    public void setLines(List<String> lines) {
        this.lines.clear();
        for (String s : lines)
            this.lines.add(new Line(s));
        if (this.lines.isEmpty())
            this.lines.add(new Line());
    }

    public void clear() {
        lines.clear();
        lines.add(new Line());
        cursor.set(0, 0);
        view.setPos(0, 0);
        cacheInvalid = true;
    }

    public void setCursor(int line, int column) {
        line = Math.max(0, Math.min(line, lines.size() - 1));
        final Line ln = lines.get(line);
        column = Math.max(0, Math.min(column, ln.sb.length()));

        cursor.set(column, line);
        refreshOffsetColumn();
    }

    public void moveCursorLine(int delta) {
        final int targetLine = Math.max(0, Math.min(cursor.y + delta, lines.size() - 1));
        if (targetLine != cursor.y) {
            cursor.y = targetLine;
            
            final Line line = getCurrentLine();
            int targetCol = fontRenderer.trimStringToWidth(line.sb.toString(), cacheCursorX).length();
            cursor.x = Math.min(targetCol, line.sb.length());

            final int currOffsetDiff = cursor.y - view.y;
            if (currOffsetDiff < 0 || currOffsetDiff >= getLinesPerView()) {
                view.y = cursor.y;
                cacheInvalid = true;
            }

            refreshOffsetColumn();
        }
    }

    public void moveCursorColumn(boolean right) {
        final StringBuilder sb = lines.get(cursor.y).sb;
        final int target = getColumnTarget(sb, right);
        if (target >= 0 && target <= sb.length()) {
            cursor.x = target;
            refreshOffsetColumn();
        } else if (target < 0) {
            if (cursor.y > 0)
                setCursor(cursor.y - 1, lines.get(cursor.y - 1).columns);
        } else {
            if (cursor.y + 1 < lines.size())
                setCursor(cursor.y + 1, 0);
        }
    }

    private void refreshCursorPos() {
        cacheCursorX = fontRenderer.getStringWidth(getCurrentLine().sb.substring(view.x, cursor.x));
    }

    private void refreshOffsetColumn() {
        if (cursor.x < view.x) {
            view.x = cursor.x;
            cacheInvalid = true;
            refreshCursorPos();
        } else {
            refreshCursorPos();
            if (view.w - cacheCursorX < viewWidthReserve) {
                final int overlap = cacheCursorX - view.w + viewWidthReserve;
                view.x += fontRenderer.trimStringToWidth(getCurrentLine().sb.toString(), overlap).length();
                cacheInvalid = true;
                refreshCursorPos();
            }
        }
    }

    public boolean remove(boolean right) {
        final Line line = getCurrentLine();
        final int target = getColumnTarget(line.sb, right);

        if (cursor.x == target)
            return false;

        final int start = Math.min(cursor.x, target);
        final int end = Math.max(cursor.x, target);

        if (start < 0) { // backspace line
            if (cursor.y == 0)
                return false;
            if (line.sb.length() == 0) {
                lines.remove(cursor.y);
                setCursor(cursor.y - 1, getLine(cursor.y - 1).sb.length());
            } else {
                final Line prevLine = lines.get(cursor.y - 1);
                final int targetColumn = prevLine.columns;
                final String leftover = prevLine.insertAtPos(line.sb.toString(), cursor.x);
                if (leftover.isEmpty())
                    lines.remove(cursor.y);
                else
                    line.remove(0, line.columns - leftover.length());
                setCursor(cursor.y - 1, targetColumn);
            }
        } else if (end > line.sb.length()) { // delete line
            if (cursor.y == lines.size() - 1)
                return false;
            if (line.sb.length() == 0) {
                lines.remove(cursor.y);
            } else {
                final Line nextLine = lines.get(cursor.y + 1);
                final String leftover = line.insertAtPos(nextLine.sb.toString(), cursor.x);
                if (leftover.isEmpty())
                    lines.remove(cursor.y + 1);
                else
                    line.remove(0, nextLine.columns - leftover.length());
            }
        } else { //
            if (line.remove(start, end) && !right)
                setCursor(cursor.y, target);
        }

        cacheInvalid = true;
        return true;
    }

    private int getColumnTarget(StringBuilder sb, boolean right) {
        if (moveByWord()) {
            final int t = right ? sb.indexOf(" ", cursor.x + 1) : sb.lastIndexOf(" ", cursor.x - 1);
            return t != -1 ? t : (right ? sb.length() : 0);
        } else {
            return cursor.x + (right ? 1 : -1);
        }
    }

    public boolean insert(char c) {
        switch (Character.getType(c)) {
            case Character.CONTROL:     // \p{Cc}
            case Character.FORMAT:      // \p{Cf}
            case Character.PRIVATE_USE: // \p{Co}
            case Character.SURROGATE:   // \p{Cs}
            case Character.UNASSIGNED:  // \p{Cn}
                return false;
        }

        final int lc = getLineCount();
        final int lw = getCurrentLine().width;

        if (!getCurrentLine().insert(c))
            if (breakLine())
                getCurrentLine().insert(c);
        final boolean inserted = lc != getLineCount() || lw != getCurrentLine().width;

        if (inserted)
            refreshOffsetColumn();

        return inserted;
    }

    public void insert(CharSequence str) {
        final String[] lines = splitter.split(str, -1);
        for (int i = 0; i < lines.length; i++) {
            inputSolidLine(lines[i]);
            if (i < lines.length - 1)
                if (!breakLine())
                    break;
        }
        cacheInvalid = true;
    }

    private void inputSolidLine(String str) {
        if (str.isEmpty())
            return;

        String overflow = getCurrentLine().insert(str);
        while (!overflow.isEmpty()) {
            if (!breakLine())
                break;
            overflow = getCurrentLine().insert(overflow);
        }

        refreshOffsetColumn();
    }

    public boolean breakLine() {
        if (getLineCount() >= maxLines)
            return false;

        final String tail = getCurrentLine().sb.substring(cursor.x);
        if (tail.isEmpty()) {
            lines.add(cursor.y + 1, new Line());
        } else {
            getCurrentLine().remove(cursor.x, cursor.x + tail.length());
            lines.add(cursor.y + 1, new Line(tail));
        }

        cursor.y++;
        cursor.x = 0;
        view.x = 0;
        cacheInvalid = true;
        refreshCursorPos();

        return true;
    }

    private static boolean moveByWord() {
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    }

    public class Line {
        public StringBuilder sb = new StringBuilder();
        public int columns = 0;
        public int width = 0;

        public Line() {

        }
        public Line(String s) {
            sb.append(s);
            columns = s.length();
            width = fontRenderer.getStringWidth(s);
        }

        public boolean insert(char c) {
            final int cw = fontRenderer.getCharWidth(c);
            if (width + cw > maxWidth)
                return false;

            sb.insert(cursor.x, c);
            columns++;
            width += cw;
            cursor.x++;
            cacheInvalid = true;

            return true;
        }

        public String insert(String s) {
            final String leftover = insertAtPos(s, cursor.x);
            cursor.x += s.length() - leftover.length();

            return leftover;
        }

        public String insertAtPos(String s, int pos) {
            final String trimmed = fontRenderer.trimStringToWidth(s, maxWidth - width);
            if (trimmed.isEmpty())
                return s;

            sb.insert(pos, trimmed);
            columns += trimmed.length();
            width += fontRenderer.getStringWidth(trimmed);
            cacheInvalid = true;

            return s.substring(trimmed.length());
        }

        public boolean remove(int start, int end) {
            if (sb.length() == 0)
                return false;

            final int w = fontRenderer.getStringWidth(sb.substring(start, end));
            sb.delete(start, end);
            columns -= end - start;
            width -= w;
            cacheInvalid = true;

            return true;
        }
    }
}
