package msifeed.mc.misca.utils;

import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.SYNC;

public class FileLogger {
    private static HashMap<String, FileLogger> loggers = new HashMap<>();

    public static synchronized FileLogger get(String filename) {
        return loggers.computeIfAbsent(filename, FileLogger::new);
    }

    public static synchronized void log(String filename, String line) {
        get(filename).log(line);
    }

    private File logFile;

    private FileLogger(String filename) {
        final File logs = new File(Minecraft.getMinecraft().mcDataDir, "logs");
        final File miscaLogs = new File(logs, "misca");
        logFile = new File(miscaLogs, filename + ".log");
        miscaLogs.mkdirs();
    }

    public void log(String line) {
        final String msg = LocalDateTime.now().toString() + " " + line + "\n";
        new Thread(() -> {
            try {
                Files.write(logFile.toPath(), msg.getBytes(UTF_8), CREATE, APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
