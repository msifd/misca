package msifeed.misca.chatex;

public class ChatexUtils {

    public static String fromAmpersandFormat(String str) {
        return str.replace('&', '\u00A7');
    }

    public static String intoAmpersandFormat(String str) {
        return str.replace('\u00A7', '&');
    }
}
