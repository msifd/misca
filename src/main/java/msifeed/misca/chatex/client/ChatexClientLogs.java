package msifeed.misca.chatex.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.GZIPOutputStream;

public class ChatexClientLogs {
    private static final SimpleDateFormat LOG_FILE_FORMAT = new SimpleDateFormat("yyyy'-'MM'-'dd");
    private static final SimpleDateFormat LOG_TIME_FORMAT = new SimpleDateFormat("HH':'mm':'ss' '");

    private static final Calendar date = Calendar.getInstance();
    private static PrintStream output;

    public static void init() {
        final Path logsDir = Paths.get("logs", "chat-" + Minecraft.getMinecraft().getSession().getUsername());
        final Path currentLog = logsDir.resolve(LOG_FILE_FORMAT.format(date.getTime()) + ".log");

        try {
            Files.createDirectories(logsDir);
            Files.list(logsDir)
                    .filter(path -> path.getFileName().toString().endsWith(".log"))
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.equals(currentLog))
                    .forEach(ChatexClientLogs::compressLog);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
//            if (!Files.exists(currentLog))
//                Files.createFile(currentLog);
            output = new PrintStream(new FileOutputStream(currentLog.toFile(), true), true, StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compressLog(Path logPath) {
        if (!Files.isRegularFile(logPath)) return;

        try {
            if (Files.size(logPath) == 0) {
                Files.delete(logPath);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final File zipFile = new File(logPath.getParent().toFile(), logPath.getFileName() + ".gz");
        try (GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(zipFile, true))) {
            Files.copy(logPath, gzip);
            Files.delete(logPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logSpeech(ITextComponent tc) {
        if (output == null) return;

        output.println(LOG_TIME_FORMAT.format(date.getTime()) + tc.getUnformattedText());
    }
}
