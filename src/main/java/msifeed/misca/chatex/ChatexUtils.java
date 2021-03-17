package msifeed.misca.chatex;

import msifeed.misca.Misca;

public class ChatexUtils {
    public static int getSpeechRange(String text) {
        int level = 0;

        for (int i = text.length() - 1; i >= 0; --i) {
            switch (text.charAt(i)) {
                case '!':
                    level++;
                case '?':
                    continue;
            }
            break;
        }

        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == '(')
                level--;
            else
                break;
        }

        return Misca.getSharedConfig().chat.getSpeechRange(level);
    }

    public static String fromAmpersandFormat(String str) {
        return str.replace('&', '\u00A7');
    }

    public static String intoAmpersandFormat(String str) {
        return str.replace('\u00A7', '&');
    }
}
