package msifeed.mc.misca.books;

public class RemoteBookParser {
    public static RemoteBook parse(String raw) throws RuntimeException {
        final RemoteBook book = new RemoteBook();

        final String cleanedRaw = raw.replaceAll("\r", "");
        if (cleanedRaw.trim().isEmpty()) throw new RuntimeException("Remote book is empty!");

        int pos;

        final int firstLineIndex = cleanedRaw.indexOf('\n');
        final String firstLine = cleanedRaw.substring(0, firstLineIndex).trim();
        pos = firstLineIndex + 1;

        if (firstLine.startsWith("#!")) {
            final String header = firstLine.substring(2);
            book.style = RemoteBook.Style.valueOf(header.toUpperCase());

            final int secondLineIndex = cleanedRaw.indexOf('\n', pos);
            if (secondLineIndex < 0) throw new RuntimeException("Book has only header!");
            book.title = cleanedRaw.substring(pos, secondLineIndex).trim();
            pos = secondLineIndex + 1;
        } else {
            book.title = firstLine;
        }

        if (pos == cleanedRaw.length()) throw new RuntimeException("Book has no body text!");
        book.text = cleanedRaw.substring(pos);

        return book;
    }
}
