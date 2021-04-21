package msifeed.misca.chatex.format;

import msifeed.misca.Misca;
import msifeed.misca.chatex.ChatexConfig;
import msifeed.misca.chatex.ChatexUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.Optional;

public class SpeechFormat {
    public static ITextComponent join(ITextComponent name, ITextComponent text) {
        return new TextComponentTranslation("misca.chatex.speech", name, text);
    }

    public static ITextComponent formatName(ITextComponent name, boolean isSelf) {
        name.getStyle().setColor(isSelf ? TextFormatting.YELLOW : TextFormatting.GREEN);

        return name;
    }

    public static Optional<ITextComponent> formatMessage(EntityPlayer self, BlockPos pos, String msg) {
        final double distance = self.getDistance(pos.getX(), pos.getY(), pos.getZ());
        final ITextComponent text = garbleficateString(msg, distance);

        return text.getUnformattedText().trim().isEmpty()
                ? Optional.empty()
                : Optional.of(text);
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
