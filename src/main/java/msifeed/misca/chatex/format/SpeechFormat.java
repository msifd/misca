package msifeed.misca.chatex.format;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.chatex.ChatexConfig;
import msifeed.misca.chatex.ChatexUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import java.util.Optional;

public class SpeechFormat {
    public static Optional<ITextComponent> format(EntityPlayer self, EntityPlayer speaker, String msg) {
        final boolean isMyMessage = self.getUniqueID().equals(speaker.getUniqueID());

        final ITextComponent textComp;
        if (isMyMessage) {
            textComp = new TextComponentString(msg);
        } else {
            textComp = garbleficateString(msg, self.getDistance(speaker));
        }

        if (textComp.getUnformattedText().trim().isEmpty())
            return Optional.empty();

        return Optional.of(new TextComponentTranslation(
                "misca.chatex.speech",
                makeNamePrefix(speaker, isMyMessage), textComp
        ));
    }

    private static ITextComponent makeNamePrefix(EntityPlayer player, boolean isSelf) {
        final ITextComponent cc = player.getDisplayName();
        cc.getStyle().setColor(isSelf ? TextFormatting.YELLOW : TextFormatting.GREEN);

        final ICharsheet cs = CharsheetProvider.get(player);
        final String wikiPage = cs.getWikiPage().isEmpty() ? cs.getName() : cs.getWikiPage();
        final String wikiUrl = Misca.getSharedConfig().chat.wikiUrlBase + wikiPage;
        cc.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, wikiUrl));

        return cc;
    }

    private static ITextComponent garbleficateString(String input, double distance) {
        if (input.isEmpty())
            return new TextComponentString("");

        final int range = ChatexUtils.getSpeechRange(input);
        final double threshold = range * Misca.getSharedConfig().chat.garble.thresholdPart;
        if (distance <= threshold)
            return new TextComponentString(input);

        final ChatexConfig.GarbleSettings settings = Misca.getSharedConfig().chat.garble;

        final double garblness = (distance - threshold) / range;

        final TextComponentString root = new TextComponentString("");
        final TextFormatting[] prevColor = {null};
        final StringBuilder sb = new StringBuilder();

        input.codePoints().forEach(cp -> {
            final double r = garblness + Math.random() / 2;

            final TextFormatting color;
            if (r > settings.miss) {
                color = prevColor[0];
            } else if (r > settings.darkGray) {
                color = TextFormatting.DARK_GRAY;
            } else if (r > settings.gray) {
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

            if (r > settings.miss) {
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
