package msifeed.mc.misca.crabs.fight;

public class FightException extends Exception {
    public final Type type;

    FightException(Type type) {
        this.type = type;
    }

    public enum Type {
        ALREADY_IN_FIGHT("asd"), NOT_IN_FIGHT("asd");

        public String msgLangItem;

        Type(String msgLangItem) {
            this.msgLangItem = msgLangItem;
        }
    }
}
