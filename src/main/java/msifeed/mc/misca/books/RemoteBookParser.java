package msifeed.mc.misca.books;

public class RemoteBookParser {
    public static RemoteBook parse(String raw) throws RuntimeException {
        final RemoteBook book = new RemoteBook();

        final int firstLineEnd = Math.min(raw.indexOf('\n'), 70);
        final String firstLine = raw.substring(0, firstLineEnd).trim();

        final boolean hasHeader = firstLine.startsWith("#!");
        if (hasHeader) {
            final String header = firstLine.substring(2);
            book.style = RemoteBook.Style.valueOf(header.toUpperCase());

            final int secondLineEnd = raw.indexOf('\n', firstLineEnd + 2);
            book.title = raw.substring(firstLineEnd, secondLineEnd).trim();
            book.text = raw.substring(secondLineEnd + 1);
        } else {
            book.title = firstLine;
            book.text = raw.substring(firstLineEnd + 1);
        }

        return book;
    }
}
