package msifeed.misca.chatex;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public enum GameMasterParams {
    INSTANCE;

    private final HashMap<UUID, Entry> settings = new HashMap<>();

    public Entry getOrCreate(UUID uuid) {
        return INSTANCE.settings.computeIfAbsent(uuid, k -> new Entry());
    }

    public boolean shouldUseGmSay(EntityPlayerMP player) {
        return player != null && Optional.ofNullable(settings.get(player.getUniqueID()))
                .map(entry -> entry.replaceSpeech)
                .orElse(false);
    }

    public static class Entry {
        public boolean replaceSpeech = false;
        public int range = 15;
        public TextFormatting color = TextFormatting.DARK_PURPLE;
        public String prefix = "";

        public ITextComponent format(String text) {
            final ITextComponent cc = new TextComponentString(prefix + (prefix.isEmpty() ? "" : " "));
            cc.getStyle().setColor(color);
            cc.appendSibling(new TextComponentString(ChatexUtils.fromAmpersandFormat(text)));
            return cc;
        }
    }
}
