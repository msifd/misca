package msifeed.mc.misca.books;

public class RemoteBookParser {
    public static RemoteBook parse(String raw) {
        final RemoteBook book = new RemoteBook();

        final int firstLineEnd = raw.indexOf('\n');
        final String firstLine = raw.substring(0, firstLineEnd);

        final boolean hasHeader = firstLine.startsWith("#!");
        if (hasHeader) {
            final String header = firstLine.substring(2).trim();
            try {
                book.style = RemoteBook.Style.valueOf(header.toUpperCase());
            } catch (Exception ignored) {
            }

            final int secondLineEnd = raw.indexOf('\n', firstLineEnd);
            book.title = raw.substring(firstLineEnd, secondLineEnd);
            book.text = raw.substring(secondLineEnd);
        } else {
            book.title = firstLine;
            book.text = raw.substring(firstLineEnd);
        }

        return book;
    }
}
