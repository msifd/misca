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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LogsSaver {
    private static final SimpleDateFormat LOG_FILE_FORMAT = new SimpleDateFormat("yyyy'-'MM'-'dd");
    private static final SimpleDateFormat LOG_TIME_FORMAT = new SimpleDateFormat("HH':'mm':'ss' '");

    private static PrintStream output;

    public static void init() {
        final Path logsDir = Paths.get("logs", "chat-" + Minecraft.getMinecraft().getSession().getUsername());
        final Path currentLog = logsDir.resolve(LOG_FILE_FORMAT.format(Calendar.getInstance().getTime()) + ".log");

        try {
            Files.createDirectories(logsDir);
            Files.list(logsDir)
                    .filter(path -> path.getFileName().toString().endsWith(".log"))
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.equals(currentLog))
                    .forEach(LogsSaver::compressLog);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
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

        final File zipFile = new File(logPath.getParent().toFile(), logPath.getFileName() + ".zip");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile))) {
            final ZipEntry entry = new ZipEntry(logPath.getFileName().toString());
            zip.putNextEntry(entry);
            Files.copy(logPath, zip);
            zip.closeEntry();

            Files.delete(logPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logSpeech(ITextComponent tc) {
        if (output == null) return;

        output.println(LOG_TIME_FORMAT.format(Calendar.getInstance().getTime()) + tc.getUnformattedText());
    }
}
