package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charstate.CharstateConfig;
import msifeed.misca.logdb.LogDB;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class OrdenceFlow {
    public static void increaseOrd(EntityPlayer player) {
        final ICharsheet sheet = CharsheetProvider.get(player);

        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final long passed = Math.min(sheet.timeSinceUpdate(), config.ordIncreaseMaxWindowSec);
        final int times = (int) (passed / config.ordIncreaseIntervalSec);
        if (times <= 0) return;

        final int increase = config.ordIncreaseAmount * times;
        sheet.resources().increase(CharResource.ord, increase);

        final long timeLeft = passed % config.ordIncreaseIntervalSec;
        sheet.setLastUpdated(System.currentTimeMillis() / 1000 - timeLeft);

        notify(player, "Вы получили " + increase + " ордеции.");
        LogDB.INSTANCE.log(player, "resource", "gain " + increase + "ord");

        convertOrdToSeal(player);
    }

    public static void convertOrdToSeal(EntityPlayer player) {
        final ICharsheet sheet = CharsheetProvider.get(player);
        final CharstateConfig config = Misca.getSharedConfig().charstate;

        final int ord = sheet.resources().get(CharResource.ord);
        final int convert = ord / config.ordToSealRate;
        if (convert <= 0) return;

        final int lost = convert * config.ordToSealRate;
        sheet.resources().increase(CharResource.ord, -lost);
        sheet.resources().increase(CharResource.seal, convert);

        notify(player, "Вы преобразовали " + lost + " ордеции в печати.");
        LogDB.INSTANCE.log(player, "resource", "convert " + lost + " ost into " + convert + " seal");
    }

    private static void notify(EntityPlayer player, String msg) {
        final ITextComponent com = new TextComponentString(msg);
        com.getStyle().setColor(TextFormatting.GRAY);
        player.sendMessage(com);
    }
}
