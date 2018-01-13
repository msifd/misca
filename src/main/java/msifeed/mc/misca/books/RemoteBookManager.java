package msifeed.mc.misca.books;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.utils.MiscaNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
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
    private Consumer<RemoteBook> fetchConsumer = null;
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
    public boolean fetchBook(String name, Consumer<RemoteBook> consumer) {
        if (fetchConsumer != null) return false;

        fetchConsumer = consumer;
        MiscaNetwork.INSTANCE.sendToServer(new MessageRemoteBook(MessageRemoteBook.Type.REQUEST_RESPONSE, name));
        return true;
    }

    public void sendCheck(String name, Consumer<Boolean> consumer) {
        if (checkConsumer != null) return;

        checkConsumer = consumer;
        MiscaNetwork.INSTANCE.sendToServer(new MessageRemoteBook(MessageRemoteBook.Type.CHECK, name));
    }

    public void receiveResponse(String rawBook) {
        if (fetchConsumer == null) return;

        final Consumer<RemoteBook> consumer = fetchConsumer;
        fetchConsumer = null;

        try {
            consumer.accept(RemoteBookParser.parse(rawBook));
        } catch (Exception ignored) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Failed to parse book file."));
        }
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

    public void signBook(EntityPlayerMP player, String name) {
        final ItemStack heldItem = player.getHeldItem();
        if (!(heldItem.getItem() instanceof ItemRemoteBook)) return;

        final String rawBook = loadBook(name);
        if (rawBook.isEmpty()) return;

        final RemoteBook book;
        try {
            book = RemoteBookParser.parse(rawBook);
        } catch (Exception ignored) {
            player.addChatMessage(new ChatComponentText("Failed to parse book file."));
            return;
        }

        final NBTTagCompound tc = new NBTTagCompound();
        tc.setString("name", name);
        tc.setString("title", book.title);
        tc.setString("style", book.style.toString());
        heldItem.setTagCompound(tc);

        heldItem.setItemDamage(book.style.ordinal() + 1);

        player.updateHeldItem();
    }
}
