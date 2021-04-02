package msifeed.misca.chatex.format;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.chatex.ChatexConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Random;

public class SpeechFormat {
    public static ITextComponent format(EntityPlayer self, EntityPlayer speaker, int range, String msg) {
        final boolean isMyMessage = self.getUniqueID().equals(speaker.getUniqueID());

        final ITextComponent textComp;
        if (isMyMessage) {
            textComp = new TextComponentString(msg);
        } else {
            final double distance = self.getDistance(speaker);
            textComp = makeTextComp(msg, distance, range);
        }

        return new TextComponentTranslation(
                "misca.chatex.speech",
                makeNamePrefix(speaker, isMyMessage), textComp
        );
    }

    private static ITextComponent makeNamePrefix(EntityPlayer player, boolean isSelf) {
        final String csName = CharsheetProvider.get(player).getName();

        final ITextComponent cc = csName.isEmpty() ? player.getDisplayName() : new TextComponentString(csName);
        cc.getStyle().setColor(isSelf ? TextFormatting.YELLOW : TextFormatting.GREEN);

        final ICharsheet cs = CharsheetProvider.get(player);
        final String wikiPage = cs.getWikiPage().isEmpty() ? cs.getName() : cs.getWikiPage();
        final String wikiUrl = Misca.getSharedConfig().chat.wikiUrlBase + wikiPage;
        cc.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, wikiUrl));

        return cc;
    }

    private static ITextComponent makeTextComp(String msg, double distance, int range) {
        final int thresholdDistance = Misca.getSharedConfig().chat.garble.thresholdDistance;

        if (distance > thresholdDistance) {
            final double garblness = (distance - thresholdDistance) / (double) range;
            return garbleficateString(msg, garblness);
        } else {
            return new TextComponentString(msg);
        }
    }

    private static TextComponentString garbleficateString(String input, double garblness) {
        if (input.isEmpty())
            return new TextComponentString("");

        final ChatexConfig.GarbleSettings settings = Misca.getSharedConfig().chat.garble;

        final Random rand = new Random();

        final TextComponentString root = new TextComponentString("");
        final TextFormatting[] prevColor = {null};
        final StringBuilder sb = new StringBuilder();

        input.codePoints().forEach(cp -> {
            final double r = garblness + rand.nextFloat() / 2;

            final TextFormatting color;
            if (r > settings.missThreshold) {
                color = prevColor[0];
            } else if (r > settings.darkGrayThreshold) {
                color = TextFormatting.DARK_GRAY;
            } else if (r > settings.grayThreshold) {
                color = TextFormatting.GRAY;
            } else {
                color = null;
            }

            if (color != prevColor[0] && sb.length() > 0) {
                final TextComponentString cc = new TextComponentString(sb.toString());
                cc.getStyle().setColor(prevColor[0]);
                root.appendSibling(cc);

                sb.setLength(0);
            }

            if (Character.isLetterOrDigit(cp) && r > settings.missThreshold) {
                sb.append(' ');
            } else {
                sb.appendCodePoint(cp);
            }

            prevColor[0] = color;
        });

        if (sb.length() > 0) {
            final TextComponentString cc = new TextComponentString(sb.toString());
            cc.getStyle().setColor(prevColor[0]);
            root.appendSibling(cc);
        }

        return root;
    }
}
