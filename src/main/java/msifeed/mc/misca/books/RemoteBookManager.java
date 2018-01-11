package msifeed.mc.misca.books;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.utils.MiscaNetwork;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;

public enum RemoteBookManager {
    INSTANCE;

    private Logger logger = LogManager.getLogger("Misca.Books");
    private Consumer<String> fetchConsumer = null;
    private Consumer<Boolean> checkConsumer = null;
    private File booksDir;

    public void preInit(FMLPreInitializationEvent event) {
        booksDir = new File(ConfigManager.config_dir, "books");
        booksDir.mkdirs();
    }

    public void init(FMLInitializationEvent event) {
        final ItemRemoteBook wikiBook = new ItemRemoteBook();
        GameRegistry.registerItem(wikiBook, "remote_book");
    }

    /**
     * @return Прошел ли запрос. Нельзя запрашивать следующую книжку до прихода предыдущей.
     */
    public boolean fetchBookText(String name, Consumer<String> consumer) {
        if (fetchConsumer != null) return false;

        fetchConsumer = consumer;
        MiscaNetwork.INSTANCE.sendToServer(new MessageRemoteBook(name, true));
        return true;
    }

    public void sendCheck(String name, Consumer<Boolean> consumer) {
        if (checkConsumer != null) return;

        checkConsumer = consumer;
        MiscaNetwork.INSTANCE.sendToServer(new MessageRemoteBook(name, true));
    }

    public void receiveResponse(String text) {
        if (fetchConsumer == null) return;

        final Consumer<String> consumer = fetchConsumer;
        fetchConsumer = null;
        consumer.accept(text);
//        consumer.accept(RemoteBookParser.parse(text));
    }

    public void receiveCheck(boolean check) {
        if (checkConsumer == null) return;

        final Consumer<Boolean> consumer = checkConsumer;
        checkConsumer = null;
        consumer.accept(check);
    }

    public boolean checkBook(String name) {
        final File bookFile = new File(booksDir, name + ".txt");
        return bookFile.exists();
    }

    public String loadBook(String name) {
        final File bookFile = new File(booksDir, name + ".txt");
        if (!bookFile.exists()) return "";

        try {
            return new String(Files.readAllBytes(bookFile.toPath()), UTF_8);
        } catch (IOException e) {
            logger.error(e);
            return "";
        }
    }

    public void signBook(EntityPlayerMP player, RemoteBook.Style style, String name) {

    }
}
