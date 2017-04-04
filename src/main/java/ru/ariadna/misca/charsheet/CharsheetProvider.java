package ru.ariadna.misca.charsheet;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import ru.ariadna.misca.Misca;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;

@SuppressWarnings("ResultOfMethodCallIgnored")
class CharsheetProvider {
    private File charsheet_dir;

    void init() {
        charsheet_dir = new File(Misca.config_dir, "charsheets");
        charsheet_dir.mkdirs();
    }

    void sendCharsheet(ICommandSender sender, String username, String text) {
        String title = LanguageRegistry.instance().getStringLocalization("misca.charsheet.title");
        String title_fmt = String.format(title, username);
        sender.addChatMessage(new ChatComponentText(title_fmt));

        try {
            for (String line : IOUtils.readLines(new StringReader(text))) {
                sender.addChatMessage(new ChatComponentText(line));
            }
        } catch (IOException e) {
            Charsheets.logger.error("Unreachable! Tried to read charsheet by line.");
        }
    }

    String readCharsheet(String username) {
        File file = new File(charsheet_dir, username + ".txt");
        if (file.isFile() && file.canRead()) {
            try {
                String text_bom = new String(Files.readAllBytes(file.toPath()), Charsets.UTF_8);
                return text_bom.replace("\uFEFF", "");
            } catch (IOException e) {
                Charsheets.logger.error("Failed to read charsheet '{}'!", username);
            }
        }
        return null;
    }

    void writeCharsheet(String username, String text) {
        File file = new File(charsheet_dir, username + ".txt");
        try {
            Files.write(file.toPath(), text.getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            Charsheets.logger.error("Failed to write charsheet '{}'! File: {}", username, file.getAbsolutePath());
        }
    }

    void removeCharsheet(String username) {
        File file = new File(charsheet_dir, username + ".txt");
        if (file.canWrite()) {
            file.delete();
        }
    }

    @SideOnly(Side.CLIENT)
    void initCharsheet(String username) {
        File file = new File(charsheet_dir, username + ".txt");
        if (!file.exists()) {
            String temp = LanguageRegistry.instance().getStringLocalization("misca.charsheet.template");
            temp = StringEscapeUtils.unescapeJava(temp);
            try {
                file.createNewFile();
                Files.write(file.toPath(), temp.getBytes(Charsets.UTF_8));
            } catch (IOException e) {
                Charsheets.logger.error("Failed to write init charsheet! File: {}", file.getAbsolutePath());
            }
        }

        // Открываем файл в редакторе
        try {
            Desktop.getDesktop().edit(file);
        } catch (Exception e) {
            Charsheets.logger.error("Failed to open charsheet in editor! :( ({})", e.getMessage());
        }
    }
}
